# Oracle执行计划
  - 执行计划是一条查询语句在Oracle中执行过程活访问路径的描述。
  
## 执行计划的常用列字段解释
  - 基数(Rows):Oracle估计的当前操作的返回结果集行数。
  - 字节(Bytes):执行该步骤后返回的字节数。
  - 耗费(COST)、CPU耗费:Oracle估计的该步骤执行成本，用于说明SQL执行的代价，理论上越小越好（该值可能与实际情况有出入）。
  - 时间（Time）:Oracle估计当前操作所需要的时间。
## 打开执行计划
1. 在PL\SQL窗口，写完select语句后按F5即可查看查询语句的执行计划。
2. 在SQL*PLUS命令终端,可以输入SET AUTOTRACE ON ,但是也有些不支持此命令。

## 看懂执行计划
  - 执行计划其实是一棵树，层次最深的最先执行，层次相同，上面的先执行。显示时已经按照层次缩进，因此从最里面的看起。最后一组就是驱动表。
> ### 1.执行顺序
  - 根据Operation缩进来判断，缩进最多的最先执行；（缩进相同时，最上面的先执行）
  - 同一级如果某个动作没有子ID就最先执行。
  - 同一级的动作执行时遵循最上最右执行的原则
> ### 表访问的几种方式
- TABLE ACCESS FULL(全表扫描)。
- TABLE ACCESS BY ROWID（通过ROWID的表存取）：ROWID是Oracle自动加在表最后的一列伪列，表中不会物理存储ROWID。
- TABLE ACCESS BY INDEX SCAN（索引扫描）
  - INDEX UNIQUE SCAN(索引唯一扫描)：每次返回至多返回一条记录
  - INDEX RANGE SCAN(索引范围扫描)：使用一个索引取多行数据
  - INDEX FULL SCAN(索引全扫描)：进行全索引扫描时，插叙出的数据都必须从索引中可以直接得到。（CBO，Cost-Based Optimization）基于代价的优化器
  - INDEX FAST FULL SCAN(索引快速扫描)：扫描索引中的所有数据块，不对查询出来的数据进行排序
  - INDEX SKIP SCAN(索引跳跃扫描)：表有一个复合索引，且在查询时有除了前导列（索引中第一列）外的其他条件，并且优化器模式为CBO。

> ### Oracle优化器
  - RBO(Rule-Based Optimization) 基于规则的优化器
  - CBO(Cost-Based optimization) 基于代价的优化器
  
## 几个概念
### 伪列-ROWID

### Recursive SQL

### Row Source and Predicate

### Driving Table

### Probed Table

### 组合索引(concatenated index)

### 可选择性(selectivity)

------
未完
