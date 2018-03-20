# 解决DruidDataSource连接超时的Case
一年前，遇到一个很棘手的问题，新写了一个项目，上线之后发现功能正常，运行也没什么问题。运行了一段时间，线上出了一个小问题，把日志下下来之后发现，每天都会有几次数据库连接超时的情况，初步怀疑是防火墙的限制，可能是数据中心设置了默认长连接保持的时间，如果检测到超过这个时间的就会主动断开，找数据中心的人确认后，发现不是这个问题导致。分析了许久，由于没有定位到具体原因，把线上问题解决了就把这个报错就搁置了一段时间，后来发现其他部门也出现了这个问题。
    
## 问题分析
再次下载日志，分析错误出现的规则，发现每次报错出现都是在访问比较少的时候（由于时间比较长，没有找到具体错误信息，有机会再贴上错误日志），但是那些请求非常频繁的项目从来都没有出现过这种问题，带着这些问题我们看了DruidDataSource心跳包检查的源码（这中间有三个主要的参数`initialSize`、`minIdle`、`maxActive`,具体解释后面会详细说），明发现DruidDataSource在发送心跳包的时候会检查当前连接的数目，如果当前连接数大于`minIdle`，DruidDataSource会断开多余连接，直到连接数等于`minIdle`，并不会检查剩下的连接是否超时；如果发现当前连接数小于或者等于`minIdle`，检查就会结束，也不会检查当前连接是否已经超时。

## 解决办法
将对数据库操作较少的项目（在心跳包检查时间间隔内可能不会有数据库操作的项目）设置`minIdle`为0

## 运行效果
修改参数后所有项目没有出现数据库连接超时的情况。


## 建议
建议以后用各种工具和第三方组件的时候，要理解清楚每个参数的意思，按照实际需求设置值，遇到问题后要冷静分析，寻找规律，不要盲目猜测，如果有能力，最好能跟到源码里面去找问题。

# DruidDataSource常用参数介绍
DRUID是阿里巴巴开源平台上一个数据库连接池实现，它结合了C3P0、DBCP、PROXOOL等DB池的优点，同时加入了日志监控，可以很好的监控DB池连接和SQL的执行情况，可以说是针对监控而生的DB连接池(据说是目前最好的连接池,不知道速度有没有BoneCP快)。

|配置|缺省值|说明|
|-|-|-|
|name||配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。 如果没有配置，将会生成一个名字，格式是："DataSource-" + System.identityHashCode(this)|
|jdbcUrl||连接数据库的url，不同数据库不一样。例如： mysql : jdbc:mysql://10.20.153.104:3306/druid2 oracle:jdbc:oracle:thin:@10.20.149.85:1521:ocnauto|
|username||连接数据库的用户名|
|password||连接数据库的密码。如果你不希望密码直接写在配置文件中，可以使用ConfigFilter。详细看这里:https://github.com/alibaba/druid/wiki/%E4%BD%BF%E7%94%A8ConfigFilter|
|driverClassName|自动识别|这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName(建议配置下)|
|initialSize|0|初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时|
|maxActive|8|最大连接池数量|
|maxIdle|8|已经不再使用，配置了也没效果|
|minIdle||最小连接池数量|
|maxWait||获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。|
|poolPreparedStatements|false|是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。|
|maxOpenPreparedStatements|-1|要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100|
|validationQuery||用来检测连接是否有效的sql，要求是一个查询语句。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会其作用。|
|testOnBorrow|true|申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。|
|testOnReturn|false|归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能|
|testWhileIdle|false|建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。|
|timeBetweenEvictionRunsMillis||有两个含义：1) Destroy线程会检测连接的间隔时间2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明|
|minEvictableIdleTimeMillis||连接保存时间|
|connectionInitSqls||物理连接初始化的时候执行的sql|
|exceptionSorter|	自动识别|当数据库抛出一些不可恢复的异常时，抛弃连接|
|filters||属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有： 监控统计用的filter:stat日志用的filter:log4j防御sql注入的filter:wall|
|proxyFilters||类型是List<com.alibaba.druid.filter.Filter>，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系|



