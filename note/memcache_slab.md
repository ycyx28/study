# Memcache内存分配
介绍memcache之前，先了解小memcache的启动脚本
```shell 
/usr/local/bin/memcached -d -m 500 -u root -l 192.168.28.221 -p 12000 -c 256 -P /tmp/memcached.pid
```
- -d选项是启动一个守护进程
- -m是分配给Memcache使用的内存数量，单位是MB，这里是500MB
- -u是运行Memcache的用户，这里是root
- -l是监听的服务器IP地址，这里指定了服务器的IP地址192.168.28.122
- -p是设置Memcache监听的端口，这里设置了12000，最好是1024以上的端口
- -c选项是最大运行的并发连接数，默认是1024，我这里设置了256，按照服务器的负载量来设定
- -P是设置保存Memcache的pid文件，这里是保存在 /tmp/memcached.pid
