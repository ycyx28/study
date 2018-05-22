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
- f 块大小增长因子，默认是1.25
- -n 最小分配空间，key+value+flags默认是48

memcached一开始并不会一下子申请500MB的内存, 而是在需要的时候才会使用malloc申请内存,当申请内存达到500MB时就不会再申请

当然，如果经常使用，可以自己写一个shell启动脚本，非常简单。

## slab
- Slab是一个内存块，它是memcached一次申请内存的最小单位。

在启动memcached的时候一般会使用参数-m指定其可用内存，但是并不是在启动的那一刻所有的内存就全部分配出去了，只有在需要的时候才会去申请，而且每次申请一定是一个slab。Slab的大小固定为1M（1048576 Byte），一个slab由若干个大小相等的chunk组成。每个chunk中都保存了一个item结构体、一对key和value。 
虽然在同一个slab中chunk的大小相等的，但是在不同的slab中chunk的大小并不一定相等，在memcached中按照chunk的大小不同，可以把slab分为很多种类（class）。在启动memcached的时候可以通过-vv来查看slab的种类。


```shell
[root@localhost memcached-1.4.5]# /usr/local/bin/memcached -d -m 10 -u root -l 192.168.49.132 -p 12005 -c 256 -P /tmp/memcached10.pid -vv
slab class   1: chunk size        96 perslab   10922
slab class   2: chunk size       120 perslab    8738
slab class   3: chunk size       152 perslab    6898
slab class   4: chunk size       192 perslab    5461
slab class   5: chunk size       240 perslab    4369
slab class   6: chunk size       304 perslab    3449
slab class   7: chunk size       384 perslab    2730
slab class   8: chunk size       480 perslab    2184
slab class   9: chunk size       600 perslab    1747
slab class  10: chunk size       752 perslab    1394
slab class  11: chunk size       944 perslab    1110
slab class  12: chunk size      1184 perslab     885
slab class  13: chunk size      1480 perslab     708
slab class  14: chunk size      1856 perslab     564
slab class  15: chunk size      2320 perslab     451
slab class  16: chunk size      2904 perslab     361
slab class  17: chunk size      3632 perslab     288
slab class  18: chunk size      4544 perslab     230
slab class  19: chunk size      5680 perslab     184
slab class  20: chunk size      7104 perslab     147
slab class  21: chunk size      8880 perslab     118
slab class  22: chunk size     11104 perslab      94
slab class  23: chunk size     13880 perslab      75
slab class  24: chunk size     17352 perslab      60
slab class  25: chunk size     21696 perslab      48
slab class  26: chunk size     27120 perslab      38
slab class  27: chunk size     33904 perslab      30
slab class  28: chunk size     42384 perslab      24
slab class  29: chunk size     52984 perslab      19
slab class  30: chunk size     66232 perslab      15
slab class  31: chunk size     82792 perslab      12
slab class  32: chunk size    103496 perslab      10
slab class  33: chunk size    129376 perslab       8
slab class  34: chunk size    161720 perslab       6
slab class  35: chunk size    202152 perslab       5
slab class  36: chunk size    252696 perslab       4
slab class  37: chunk size    315872 perslab       3
slab class  38: chunk size    394840 perslab       2
slab class  39: chunk size    493552 perslab       2
slab class  40: chunk size    616944 perslab       1
slab class  41: chunk size    771184 perslab       1
slab class  42: chunk size   1048576 perslab       1
<26 server listening (auto-negotiate)
<27 send buffer was 124928, now 268435456
<27 server listening (udp)
<27 server listening (udp)
<27 server listening (udp)
<27 server listening (udp)
```
memcached把slab分为42类（class1～class42），在class 1中，chunk的大小为96字节，由于一个slab的大小是固定的1048576字节（1M），因此在class1中最多可以有perslab = 10922个chunk：
```other
10922×80 + 64 = 1048576
```
在class1中，剩余的64字节因为不够一个chunk的大小（96byte），因此会被浪费掉

## chunk
- Chunk是存放缓存数据的单位。

Chunk是一系列固定的内存空间，这个大小就是管理它的slab的最大存放大小，chunk块的大小可以为96B,120B,152B...1184KB.使用何种大小的chunk块是由memcache根据数据的长度来决定的。

例如：slab 1的所有chunk都是128byte，而slab 4的所有chunk都是256byte。chunk是memcached实际存放缓存数据的地方，因为chunk的大小固定为slab能够存放的最大值， 所以所有分配给当前slab的数据都可以被chunk存下。如果时间的数据大小小于chunk的大小，空余的空间将会被闲置，这个是为了防止内存碎片而设计的。例如，chunk size是256byte，而存储的数据只有200byte，剩下的56byte将被闲置。

chunk计算公式:
```other
chunk size(class i) = (default_size+item_size)*f^(i-1) + CHUNK_ALIGN_BYTES
```
- default_size：默认大小为48字节,也就是memcached默认的key+value的大小为48字节，可以通过-n参数来调节其大小；
- item_size：item结构体的长度，固定为48字节。default_size大小为48字节,item_size为48，因此class1的chunk大小为48+48=96字节；
- f为factor，是chunk变化大小的因素，默认值为1.25，调节f可以影响chunk的步进大小，在启动时可以使用-f来指定;
- CHUNK_ALIGN_BYTES是一个修正值，用来保证chunk的大小是某个值的整数倍（在32位机器上要求chunk的大小是4的整数倍）

所以可以根据自己的业务需求，通过-f 和 -n参数，来合理划分trunk的大小
```shell
[root@localhost memcached-1.4.5]# /usr/local/bin/memcached -d -m 10 -u root -l 192.168.49.132 -p 12005 -c 256 -P /tmp/memcached10.pid -n 80 -f 2 -vv
slab class   1: chunk size       128 perslab    8192
slab class   2: chunk size       256 perslab    4096
slab class   3: chunk size       512 perslab    2048
slab class   4: chunk size      1024 perslab    1024
slab class   5: chunk size      2048 perslab     512
slab class   6: chunk size      4096 perslab     256
slab class   7: chunk size      8192 perslab     128
slab class   8: chunk size     16384 perslab      64
slab class   9: chunk size     32768 perslab      32
slab class  10: chunk size     65536 perslab      16
slab class  11: chunk size    131072 perslab       8
slab class  12: chunk size    262144 perslab       4
slab class  13: chunk size    524288 perslab       2
slab class  14: chunk size   1048576 perslab       1
[root@localhost memcached-1.4.5]# <26 server listening (auto-negotiate)
<27 send buffer was 124928, now 268435456
<27 server listening (udp)
<27 server listening (udp)
<27 server listening (udp)
<27 server listening (udp)

```
可以看见class2的chunk大小为：(80+48)*2^(2-1)=256字节,根据具体的业务预估缓存数据的最小值以便设置memcache的chunk初始值，避免内存浪费。

memcache默认的slab大小是1M，所以不能存入大小超过1M的数据，但一旦需要存入大数据时可以使用-I参数来设置slab的值,但是不推荐将slab值设置为超过1M

## 总结
为了避免使用memcached时出现异常, 使用memcached的项目需要注意:
- 最好不要往memcached存储一个大于1MB的数据，如果需要，需要通过`-i`修改参数。
- 往memcached存储的所有数据,如果数据的大小分布于各种chunk大小区间,从64B到1MB都有,可能会造成内存的极大浪费以及memcached的异常.

例如：
```other
memcached最大可申请内存为2M, 你第一次存储一个10B的数据,那么memcached会申请1MB的内存,以96B进行分割然后存储该数据, 第二次存储一个100B的数据,那么memcached会继续申请1M的内存,以120B进行分割然后存储该数据, 第三次如果你想存储一个150B的数据, 如果可以继续申请内存, memcached会申请1M内存以152B的大小进行分割, 但是由于最大可申请仅仅为2MB,所以会导致该数据无法存储.
```

