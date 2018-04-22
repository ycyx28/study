# Redis cluster 搭建
- 1.获取redis
``` shell
wget http://download.redis.io/releases/redis-4.0.9.tar.gz
tar -xvf redis-4.0.9.tar.gz
```

- 2.编译安装
```shell
make 
make install prefix=/usr/local/redis-cluster/master1
```

- 3.配置redis.conf
```xml
#后台启动
daemonize yes

#修改端口号，从7001到7006
port 7001

bind 192.168.28.131

cluster-enabled yes
#开启cluster，去掉注释

cluster-config-file /usr/local/redis-cluster/master1/conf/nodes.conf

pidfile /usr/local/redis-cluster/master1/conf/redis_7001.pid

dbfilename dump.rdb

dir /usr/local/redis-cluster/master1/data

cluster-node-timeout 15000

appendonly yes
```

- 4.创建多个node节点

修改每个node节点的redis.conf文件
```shell
cd /usr/local/redis-cluster/master1
mkdir conf
vim conf/redis.conf
mkdir data
cd ..
cp -r master1 master2
cp -r master1 master3
cp -r master1 slave1
cp -r master1 slave2
cp -r master1 slave3
cp redis-4.0.9/src/redis-trib.rb /usr/local/redis-cluster
```

- 5.安装ruby
```shell
yum install centos-release-scl-rh 　　//会在/etc/yum.repos.d/目录下多出一个CentOS-SCLo-scl-rh.repo源
yum install rh-ruby23  -y　　　　//直接yum安装即可
scl  enable  rh-ruby23 bash　　　　//必要一步
ruby -v　　　　//查看安装版本
gem install redis
```
备注：配置ruby,重启机器后ruby可以正常使用，`ruby -v`

在/etc/profile中添加类似如下两句：
```shell
source /opt/rh/rh-ruby23/enable
export X_SCLS="`scl enable rh-ruby23 'echo $X_SCLS'`"
```

- 6.启动redis

在redis-cluster文件夹下创建启动脚本，start-all.sh,启动脚本如下
```shell
#!/bin/bash
cd master1
./bin/redis-server conf/redis.conf
cd ..
cd master2
./bin/redis-server conf/redis.conf
cd ..
cd master3
./bin/redis-server conf/redis.conf
cd ..
cd slave1
./bin/redis-server conf/redis.conf
cd ..
cd slave2
./bin/redis-server conf/redis.conf
cd ..
cd slave3
./bin/redis-server conf/redis.conf
cd ..
```

```shell
cd redis-cluser
vim start-all.sh
chmod 755 start-all.sh
./start-all.sh
ps -ef | grep redis
```

- 7.停止redis

在redis-cluster文件夹下创建停止脚本，shutdown-all.sh,启动脚本如下
```shell
#!/bin/bash

PIDS=`ps -ef | grep redis | grep -v "grep redis" | awk '{print $2}'`
if [ -z "$PIDS" ]; then
        echo "ERROR: The redis-cluster does not started!"
        exit 1
fi

echo -e "Stopping the redis cluster ...\c"
for PID in $PIDS ; do
        kill -9 $PID >/dev/null 2>&1
done

COUNT=`ps -ef | grep redis | grep -v "grep redis" | awk '{print $2}' | wc -l`

echo $COUNT
echo "PID:$PIDS"
```

```shell
cd redis-cluser
vim shutdown-all.sh
chmod 755 shutdown-all.sh
./shutdown-all.sh
ps -ef | grep redis
```

- 8.启动redis cluster
```shell
./redis-4.0.1/src/redis-trib.rb create  --replicas 1 192.168.28.131:7001 192.168.28.131:7002 192.168.28.131:7003 192.168.28.131:7004 192.168.28.131:7005 192.168.28.131:7006
```
--replicas参数指定集群中每个主节点配备几个从节点，这里设置为1

redis cluster启动脚本cluster-create.sh
```shell
#!/bin/bash
./redis-4.0.1/src/redis-trib.rb create  --replicas 1 192.168.28.131:7001 192.168.28.131:7002 192.168.28.131:7003 192.168.28.131:7004 192.168.28.131:7005 192.168.28.131:7006
```
使用
```shell
chmod 755 cluster-create.sh
./cluster-create.sh
```

- 9连接redis cluster
```shell
./bin/redis-cli -c -h 192.168.28.132 -p 7001
```
-c cluster，表示使用cluster集群方式启动

-h host，机器ip

-p port,redis启动端口

- 10.redis3.0也可以按此方法安装，ruby安装可以简化,下面简单介绍下
```shell
yum install ruby
yum install rubygems
wget https://rubygems.global.ssl.fastly.net/gems/redis-3.2.2.gem
ruby install -l redis-3.2.2.gem
```

- 11.集群停机重启

当集群启动后，若有redis节点重启或者挂掉，不需要做其他处理，把挂掉的redis节点启动后整个集群即可进入正常工作，如果需要重置集群，需要把集群中的生成的配置文件产出后再从新启动，删除脚本如下：
```shell
rm -rf master*/conf/nodes.conf master*/conf/redis_7001.pid master*/data/*
rm -rf slave*/conf/nodes.conf slave*/conf/redis_7001.pid slave*/data/*
```

