# Redis cluster 搭建

- 1.添加master节点
	- 在集群中新增一个节点7007作为主节点修改配置文件
	- 启动7007redis服务
	- 将加7007加入集群中。添加使用redis-trib.rb的add-node命令
	- `./redis-trib.rb add-node 192.168.28.132:7007 192.168.28.132:7002`
	
	add-node是加入集群节点，192.168.28.132:7007为要加入的节点，192.168.28.132:7002 表示加入的集群的一个节点，用来辨识是哪个集群，理论上这个集群的节点都可以。
   
执行以下add-node
```shell
[root@node2 bin]# ./redis-trib.rb add-node 192.168.28.132:7007 192.168.28.132:7002
>>> Adding node 192.168.28.132:7007 to cluster 192.168.28.132:7002
>>> Performing Cluster Check (using node 192.168.28.132:7002)
M: 120b361a340531bfae69e2a85b2687737245578d 192.168.28.132:7002
   slots:5461-10922 (5462 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:0-5460 (5461 slots) master
   1 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:10923-16383 (5461 slots) master
   1 additional replica(s)
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
>>> Send CLUSTER MEET to node 192.168.28.132:7007 to make it join the cluster.
[OK] New node added correctly.



[root@node2 bin]# ./redis-trib.rb check 192.168.28.132:7007
>>> Performing Cluster Check (using node 192.168.28.132:7007)
M: d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7007
   slots: (0 slots) master
   0 additional replica(s)
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: 120b361a340531bfae69e2a85b2687737245578d 192.168.28.132:7002
   slots:5461-10922 (5462 slots) master
   1 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:10923-16383 (5461 slots) master
   1 additional replica(s)
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:0-5460 (5461 slots) master
   1 additional replica(s)
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.

```

redis-cluster在新增节点时并未分配卡槽，需要我们手动对集群进行重新分片迁移数据，需要重新分片命令 reshard

` redis-trib.rb reshard 192.168.28.132:7001`, 这个命令是用来迁移slot节点的，后面的192.168.28.132:7001是表示是哪个集群，集群中任意节点均可，执行结果如下：

```shell
[root@node2 bin]# ./redis-trib.rb reshard 192.168.28.132:7001
>>> Performing Cluster Check (using node 192.168.28.132:7001)
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:0-5460 (5461 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7007
   slots: (0 slots) master
   0 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:10923-16383 (5461 slots) master
   1 additional replica(s)
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
M: 120b361a340531bfae69e2a85b2687737245578d 192.168.28.132:7002
   slots:5461-10922 (5462 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
How many slots do you want to move (from 1 to 16384)? 
```

它提示我们需要迁移多少slot到7007上，我们平分16384个哈希槽给4个节点：16384/4 = 4096，我们需要移动4096个槽点到7007上

```shell
How many slots do you want to move (from 1 to 16384)? 4096
What is the receiving node ID? 
```
需要输入7007的节点id，d9180d91a208653f2b6c116569b58948073503aa

```shell
What is the receiving node ID? d9180d91a208653f2b6c116569b58948073503aa
Please enter all the source node IDs.
  Type 'all' to use all the nodes as source nodes for the hash slots.
  Type 'done' once you entered all the source nodes IDs.
Source node #1:
```

redis-trib 会向你询问重新分片的源节点（source node），即，要从特点的哪个节点中取出 4096 个哈希槽，还是从全部节点提取4096个哈希槽， 并将这些槽移动到7007节点上面。

如果我们不打算从特定的节点上取出指定数量的哈希槽，那么可以向redis-trib输入 all，这样的话， 集群中的所有主节点都会成为源节点，redis-trib从各个源节点中各取出一部分哈希槽，凑够4096个，然后移动到7007节点上：

```shell
Source node #1:all 
    ...
    Moving slot 12280 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12281 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12282 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12283 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12284 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12285 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12286 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
    Moving slot 12287 from 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
Do you want to proceed with the proposed reshard plan (yes/no)?yes
```
后开始从别的主节点迁移哈希槽，并且确认。

重新分配后check一下节点的分配情况

```shell
[root@node2 bin]# ./redis-trib.rb check 192.168.28.132:7001
>>> Performing Cluster Check (using node 192.168.28.132:7001)
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:1365-5460 (4096 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7007
   slots:0-1364,5461-6826,10923-12287 (4096 slots) master
   0 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:12288-16383 (4096 slots) master
   1 additional replica(s)
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
M: 120b361a340531bfae69e2a85b2687737245578d 192.168.28.132:7002
   slots:6827-10922 (4096 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```

`slots:0-1364,5461-6826,10923-12287 (4096 slots) master`

可以看到7007节点分片的哈希槽片不是连续的,而是从原有每个master节点的槽中分配一部分大7007节点。

- 2.新增slave节点
	- 在集群中新增一个节点7008作为主节点修改配置文件
	- 启动7008redis服务
	- 将加7008加入集群中。添加使用redis-trib.rb的add-node --slave命令
	- `./redis-trib.rb add-node --slave --master-id [nodeid] 192.168.28.132:7008 192.168.28.132:7002`
	
nodeid为要加到master主节点的node id,这里给7007增加从节点，nodeid为`d9180d91a208653f2b6c116569b58948073503aa`，
192.168.28.132:7008为新增的从节点，192.168.28.132:7002为集群的一个节点（集群的任意节点都行），用来辨识是哪个集群；
如果没有给定--master-id的话，redis-trib将会将新增的从节点随机到从节点较少的主节点上。
   
```shell
[root@node2 bin]# ./redis-trib.rb add-node --slave --master-id d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7008 192.168.28.132:7002
>>> Adding node 192.168.28.132:7008 to cluster 192.168.28.132:7002
>>> Performing Cluster Check (using node 192.168.28.132:7002)
M: 120b361a340531bfae69e2a85b2687737245578d 192.168.28.132:7002
   slots:6827-10922 (4096 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:1365-5460 (4096 slots) master
   1 additional replica(s)
M: d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7007
   slots:0-1364,5461-6826,10923-12287 (4096 slots) master
   0 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:12288-16383 (4096 slots) master
   1 additional replica(s)
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
>>> Send CLUSTER MEET to node 192.168.28.132:7008 to make it join the cluster.
Waiting for the cluster to join.
>>> Configure node as replica of 192.168.28.132:7007.
[OK] New node added correctly.
```

slave添加成功，check一下7008：

```shell
[root@node2 bin]# ./redis-trib.rb check 192.168.28.132:7008
>>> Performing Cluster Check (using node 192.168.28.132:7008)
S: b82b2a13800f87c7e61eea2f9c60064c87a4cbc5 192.168.28.132:7008
   slots: (0 slots) slave
   replicates d9180d91a208653f2b6c116569b58948073503aa
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:1365-5460 (4096 slots) master
   1 additional replica(s)
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
M: d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7007
   slots:0-1364,5461-6826,10923-12287 (4096 slots) master
   1 additional replica(s)
M: 120b361a340531bfae69e2a85b2687737245578d 192.168.28.132:7002
   slots:6827-10922 (4096 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:12288-16383 (4096 slots) master
   1 additional replica(s)
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.s
```

slave节点添加成功
