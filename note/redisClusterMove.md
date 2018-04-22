# Redis cluster 节点移除

## 1.移除master节点

master节点删除比较麻烦，需要先将对于master节点的Hash槽转移后才能进行删除，下面为删除slots操作。

`./redis-trib.rb reshard 192.168.28.132:7001`,192.168.28.132:7001也可以为集群中任意节点，这里模拟删除7007节点
，将7007节点slots转移到7001处。

```shell
[root@node2 bin]# ./redis-trib.rb reshard 192.168.28.132:7001
>>> Performing Cluster Check (using node 192.168.28.132:7001)
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:1365-5460 (4096 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
M: d9180d91a208653f2b6c116569b58948073503aa 192.168.28.132:7007
   slots:0-1364,5461-6826,10923-12287 (4096 slots) master
   1 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:12288-16383 (4096 slots) master
   1 additional replica(s)
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
S: b82b2a13800f87c7e61eea2f9c60064c87a4cbc5 192.168.28.132:7008
   slots: (0 slots) slave
   replicates d9180d91a208653f2b6c116569b58948073503aa
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
How many slots do you want to move (from 1 to 16384)?
```
询问需要转移多少个slots，7007节点一共有4096个slot

```shell
How many slots do you want to move (from 1 to 16384)? 4096
What is the receiving node ID?
```

这里输入接收的节点id，即node1的id:`260bddd8ee201d8119eb09d71119461de654320c`。

```shell
What is the receiving node ID? 260bddd8ee201d8119eb09d71119461de654320c
Please enter all the source node IDs.
  Type 'all' to use all the nodes as source nodes for the hash slots.
  Type 'done' once you entered all the source nodes IDs.
Source node #1:
```
询问需要移动到全部主节点上还是单个主节点，这里输入node1的id:`d9180d91a208653f2b6c116569b58948073503aa`

```shell
Source node #1:d9180d91a208653f2b6c116569b58948073503aa
Source node #2:done
.
.
.
Moving slot 12281 from d9180d91a208653f2b6c116569b58948073503aa
Moving slot 12282 from d9180d91a208653f2b6c116569b58948073503aa
Moving slot 12283 from d9180d91a208653f2b6c116569b58948073503aa
Moving slot 12284 from d9180d91a208653f2b6c116569b58948073503aa
Moving slot 12285 from d9180d91a208653f2b6c116569b58948073503aa
Moving slot 12286 from d9180d91a208653f2b6c116569b58948073503aa
Moving slot 12287 from d9180d91a208653f2b6c116569b58948073503aa
Do you want to proceed with the proposed reshard plan (yes/no)? yes
```
确认之后会一个一个将7007的卡槽移到到7001上。

check7007节点slots信息

```shell
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
   slots:6827-10922 (4096 slots) master
   1 additional replica(s)
M: 6303243c1c248b121c47fc8adf44d7e5a66ffe3f 192.168.28.132:7003
   slots:12288-16383 (4096 slots) master
   1 additional replica(s)
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:0-6826,10923-12287 (8192 slots) master
   2 additional replica(s)
S: b82b2a13800f87c7e61eea2f9c60064c87a4cbc5 192.168.28.132:7008
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```
移除成功，7007节点slots: (0 slots) master

下面执行del-node操作，移除节点，节点删除时必须知道删除节点的nodeid，如果7007的节点id：`d9180d91a208653f2b6c116569b58948073503aa`。

```shell
[root@node2 bin]# ./redis-trib.rb del-node 192.168.28.132:7007 d9180d91a208653f2b6c116569b58948073503aa
>>> Removing node d9180d91a208653f2b6c116569b58948073503aa from cluster 192.168.28.132:7007
>>> Sending CLUSTER FORGET messages to the cluster...
>>> SHUTDOWN the node.

```

移除成功，check集群节点状态：

```shell
[root@node2 bin]# ./redis-trib.rb check 192.168.28.132:7008
>>> Performing Cluster Check (using node 192.168.28.132:7008)
S: b82b2a13800f87c7e61eea2f9c60064c87a4cbc5 192.168.28.132:7008
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:0-6826,10923-12287 (8192 slots) master
   2 additional replica(s)
S: 45007a3d04105cbf982e19ae4d50bbcad756f4ef 192.168.28.132:7005
   slots: (0 slots) slave
   replicates 6303243c1c248b121c47fc8adf44d7e5a66ffe3f
S: b8d77e5fdadc312876af6d0c1dec78109a3fa0a8 192.168.28.132:7006
   slots: (0 slots) slave
   replicates 260bddd8ee201d8119eb09d71119461de654320c
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
[OK] All 16384 slots covered.
```
不存在7007节点信息，结束。

## 2.移除slave节点

移除slave节点相对比较简单，因为slave节点没有分配slots，可以直接执行del-node。

下面我们模拟删除7008节点。

```shell
[root@node2 bin]# ./redis-trib.rb del-node 192.168.28.132:7008 b82b2a13800f87c7e61eea2f9c60064c87a4cbc5
>>> Removing node b82b2a13800f87c7e61eea2f9c60064c87a4cbc5 from cluster 192.168.28.132:7008
>>> Sending CLUSTER FORGET messages to the cluster...
>>> SHUTDOWN the node.

[root@node2 bin]# ./redis-trib.rb check 192.168.28.132:7001
>>> Performing Cluster Check (using node 192.168.28.132:7001)
M: 260bddd8ee201d8119eb09d71119461de654320c 192.168.28.132:7001
   slots:0-6826,10923-12287 (8192 slots) master
   1 additional replica(s)
S: f5dd71649bafb31fcf2a17621f5714cc175b1e05 192.168.28.132:7004
   slots: (0 slots) slave
   replicates 120b361a340531bfae69e2a85b2687737245578d
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
删除成功，check无7008节点信息。

	
