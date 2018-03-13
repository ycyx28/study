# Spring事物管理

事务是一系列的动作，一旦其中有一个动作出现错误，必须全部回滚。事务的出现是为了确保数据的完整性和一致性

## 事务的四大特性（ACID）

1. 原子性（Atomicity）：事务是一个原子操作，由一系列动作组成。事务的原子性确保动作要么全部完成，要么完全不起作用。

2. 一致性（Consistency）：事务在完成时，必须是所有的数据都保持一致状态。

3. 隔离性（Isolation）：并发事务执行之间无影响，在一个事务内部的操作对其他事务是不产生影响，这需要事务隔离级别来指定隔离性。

4. 持久性（Durability）：一旦事务完成，数据库的改变必须是持久化的

## 事物的隔离级别

1. 事物并发可能会导致一些问题

- 脏读：一个事务读到另一个事务未提交的更新数据。 
- 不可重复读：一个事务两次读同一行数据，可是这两次读到的数据不一样。 
- 幻读：一个事务执行两次查询，但第二次查询比第一次查询多出了一些数据行。 
- 丢失更新：撤消一个事务时，把其它事务已提交的更新的数据覆盖了

2. 隔离级别
- TRANSACTION_NONE JDBC 驱动不支持事务 
- TRANSACTION_READ_UNCOMMITTED 允许脏读、不可重复读和幻读。 
- TRANSACTION_READ_COMMITTED 禁止脏读，但允许不可重复读和幻读。 （常用）
- TRANSACTION_REPEATABLE_READ 禁止脏读和不可重复读，单运行幻读。 
- TRANSACTION_SERIALIZABLE 禁止脏读、不可重复读和幻读。

## Spring事务
有了Spring，我们再也无需要去处理获得连接、关闭连接、事务提交和回滚等这些操作，使得我们把更多的精力放在处理业务上。事实上Spring并不直接管理事务，而是提供了多种事务管理器。他们将事务管理的职责委托给Hibernate或者JTA等持久化机制所提供的相关平台框架的事务来实现。
### Spring事务的传播属性
|名称| 值 |解释|
|-| :-: |-|
|PROPAGATION_REQUIRED | 0 |支持当前事务，如果当前没有事务，就新建一个事务。这是最常见的选择，也是Spring默认的事务的传播。|
|PROPAGATION_SUPPORTS | 1 | 支持当前事务，如果当前没有事务，就以非事务方式执行。 |
|PROPAGATION_MANDATORY | 2 |支持当前事务，如果当前没有事务，就抛出异常。 |
|PROPAGATION_REQUIRES_NEW | 3 |新建事务，如果当前存在事务，把当前事务挂起。 |
|PROPAGATION_NOT_SUPPORTED | 4 |以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。|
|PROPAGATION_NEVER | 5 |以非事务方式执行，如果当前存在事务，则抛出异常。 |
|PROPAGATION_NESTED| 6 |如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则进行与PROPAGATION_REQUIRED类似的操作。|

### Spring事务的隔离级别
|名称| 值 |解释|
|-| :-: |-|
|ISOLATION_DEFAULT | -1 |这是一个PlatfromTransactionManager默认的隔离级别，使用数据库默认的事务隔离级别。另外四个与JDBC的隔离级别相对应|
|ISOLATION_READ_UNCOMMITTED | 1 | 这是事务最低的隔离级别，它充许另外一个事务可以看到这个事务未提交的数据。这种隔离级别会产生脏读，不可重复读和幻读。 |
|ISOLATION_READ_COMMITTED | 2 |保证一个事务修改的数据提交后才能被另外一个事务读取。另外一个事务不能读取该事务未提交的数据。 |
|ISOLATION_REPEATABLE_READ | 4 |这种事务隔离级别可以防止脏读，不可重复读。但是可能出现幻读。 |
|ISOLATION_SERIALIZABLE | 8 |这是花费最高代价但是最可靠的事务隔离级别。事务被处理为顺序执行。除了防止脏读，不可重复读外，还避免了幻读。|

### 配置事务管理器
``` xml
<!-- 配置事务 -->
	<bean id="transactionManagerOrder" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource">
			<ref local="dataSourceAccount" />
		</property>
	</bean>
```

### 编程式事务管理
``` xml
<!--配置事务管理的模板-->
    <bean id="transactionTemplate" class="org.springframework.transaction.support.TransactionTemplate">
        <property name="transactionManager" ref="transactionManagerOrder"></property>
        <!--定义事务隔离级别,-1表示使用数据库默认级别-->
        <property name="isolationLevelName" value="ISOLATION_DEFAULT"></property>
        <property name="propagationBehaviorName" value="PROPAGATION_REQUIRED"></property>
    </bean>
```

### 声明式事物管理
``` xml
<!--
	propagation:配置传播属性
	rollback-for:配置异常回滚方法
	name：控制事物方法，如果以“insert”开头的方法insertOrder(Order order)，
	事物传播属性为“REQUIRED”,其他方法事物传播属性为“SUPPORTS”，一般查询的时候用到
-->
<tx:advice id="txAdviceOrder" transaction-manager="transactionManagerOrder">
        <tx:attributes>
            <tx:method name="insert*" propagation="REQUIRED" read-only="false"  rollback-for="Exception"/>
	    <tx:method name="*" propagation="SUPPORTS" read-only="true" />
        </tx:attributes>
</tx:advice>

<!--
	expression：用aop配置事物控制的包、类和方法，多个用OR 或者 || 分隔
-->
 <aop:config>
        <aop:pointcut id="servicePointcutOrder" expression="execution (* com.ycyx28.study.orderimpl.*.*(..)) OR
						execution (* com.ycyx28.study.hisorderimpl.*.*(..))"/>
        <aop:advisor advice-ref="txAdviceOrder" pointcut-ref="servicePointcutOrder"/>
 </aop:config>

```

### 基于注解事物管理(@Transactional)

``` xml
 <!-- 声明式事务管理 配置事物的注解方式注入-->
    <tx:annotation-driven transaction-manager="transactionManager"/>
```
 在对应方法上加上@Transactional注解，并指定异常回滚
``` java
@Transactional(rollbackFor=BaseRuntimeException.class)
public void insertOrder(Order order) throws Exception {
    try{
	dao.save("insertOrder",order);
    }catch (Exception e) {
	throw new BaseRuntimeException("insertOrder exception!!!",e);
    }	
}
```

