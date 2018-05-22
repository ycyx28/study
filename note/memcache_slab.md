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

memcached一开始并不会一下子申请500MB的内存, 而是在需要的时候才会使用malloc申请内存,当申请内存达到500MB时就不会再申请

当然，如果经常使用，可以自己写一个shell启动脚本，非常简单。

## slab
- Slab是一个内存块，它是memcached一次申请内存的最小单位。

在启动memcached的时候一般会使用参数-m指定其可用内存，但是并不是在启动的那一刻所有的内存就全部分配出去了，只有在需要的时候才会去申请，而且每次申请一定是一个slab。Slab的大小固定为1M（1048576 Byte），一个slab由若干个大小相等的chunk组成。每个chunk中都保存了一个item结构体、一对key和value。 
虽然在同一个slab中chunk的大小相等的，但是在不同的slab中chunk的大小并不一定相等，在memcached中按照chunk的大小不同，可以把slab分为很多种类（class）。在启动memcached的时候可以通过-vv来查看slab的种类

## chunk
- Chunk是存放缓存数据的单位。

Chunk是一系列固定的内存空间，这个大小就是管理它的slab的最大存放大小，chunk块的大小可以为64B,128B,256B...1024KB.使用何种大小的chunk块是由memcache根据数据的长度来决定的。

例如：slab 1的所有chunk都是104byte，而slab 4的所有chunk都是280byte。chunk是memcached实际存放缓存数据的地方，因为chunk的大小固定为slab能够存放的最大值， 所以所有分配给当前slab的数据都可以被chunk存下。如果时间的数据大小小于chunk的大小，空余的空间将会被闲置，这个是为了防止内存碎片而设计的。例如，chunk size是224byte，而存储的数据只有200byte，剩下的24byte将被闲置
