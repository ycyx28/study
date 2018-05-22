# Redis持久化策略
redis和memcache都是缓存服务器，redis支持缓存落地到磁盘，可以持久化，而memcache数据存储在内存中，断掉或者服务宕机了，数据就全部丢失了。Redis执行2种持久化策略。

- rdb:快照形式是直接把内存中的数据保存到一个dump文件中，定时保存，保存策略
- aof：把所有的对redis的服务器进行修改的命令都存到一个文件里，命令的集合

## rdb
默认情况下，Redis使用的是快照rdb的持久化方式，将内存中的数据以快照的方式写入二进制文件中，默认的文件名是dump.rdb

redis的配置文件如下:
```xml
save 900 1 
save 300 10
save 60 10000

port 6379
bind 192.168.4.11

dir  /usr/local/redis_cluster/master/data
dbfilename dump.rdb
```
save解释：
- `save 900 1`：900秒之内，如果超过1个key被修改，则发起快照保存；
- `save 300 10`：300秒内，如果超过10个key被修改，则发起快照保存；
- `save 60 1`：60秒之内，如果1万个key被修改，则发起快照保存；


这种持久化策略在一定程度上可以保证数据持久化，但是因为是定时保存，当redis宕机就会丢失部分数据；
如果数据量特别大，写操作频繁，频繁的进行IO操作，会影响机器性能。


## AOF
使用aof做持久化，每一个写命令都通过write函数追加到appendonly.aof中。

开启AOF配置如下：
``` xml
appendonly yes
```

文件生成如下：
```shell
[root@localhost data]# ll
total 8
-rw-r--r--. 1 root root 69 May 22 09:23 appendonly.aof
-rw-r--r--. 1 root root 78 May 22 09:23 dump.rdb
```

appendonly.aof数据内容如下：
```shell
[root@localhost data]# vim appendonly.aof 
*2
$6
SELECT
$1
0
*3
$3
set
$6
dinpay
$14
www.dinpay.com
```

