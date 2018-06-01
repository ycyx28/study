# elasticsearch-5.6.3集群安装

elasticsearch.yml
``` other

# ---------------------------------- Cluster -----------------------------------
#
# Use a descriptive name for your cluster:
# 集群名称，以此来判断属于哪个集群
cluster.name: test_es_cluster
#
# ------------------------------------ Node ------------------------------------
#
# Use a descriptive name for the node:
#节点名称
node.name: node2
# Path to directory where to store the data (separate multiple locations by comma):
# 数据保存路径
path.data: /home/yc/es/data
#
# Path to log files:
# 日志保存路径
path.logs: /home/yc/es/logs
#
bootstrap.memory_lock: false
bootstrap.system_call_filter: false

# ---------------------------------- Network -----------------------------------
#
# Set the bind address to a specific IP (IPv4 or IPv6):
#ip
network.host: 192.168.4.11
#
# Set a custom port for HTTP:
# 端口
http.port: 9200
#

#设置节点间交互的tcp端口，默认是19300
transport.tcp.port: 19300
#
#设置是否压缩tcp传输时的数据，默认为false，不压缩
transport.tcp.compress: true

# --------------------------------- Discovery ----------------------------------
#
# Pass an initial list of hosts to perform discovery when new node is started:
# The default list of hosts is ["127.0.0.1", "[::1]"]
# 被集群发现的节点
discovery.zen.ping.unicast.hosts: ["192.168.4.10:19300", "192.168.4.11:19300","192.168.4.12:19300"]
#
# Prevent the "split brain" by configuring the majority of nodes (total number of master-eligible nodes / 2 + 1):
# 备选为master节点个数，候选节点
discovery.zen.minimum_master_nodes: 1
#
#head插件配置
http.cors.enabled: true
http.cors.allow-origin: "*"

```

## Kibana-5.6.3

kibana.yml
``` other
# Kibana is served by a back end server. This setting specifies the port to use.
server.port: 5601

server.host: "192.168.4.10"

# The URL of the Elasticsearch instance to use for all your queries.
elasticsearch.url: "http://192.168.4.10:9200"



```


