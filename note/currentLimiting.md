# 限流
在高并发系统开发中，常常会遇到资源不足，这里资源足有很多方面，比如数据库资源、服务的QPS/TPS达到极限、第三方请求资源不够（如银行转账频率限制）等，为了
系统稳健提供服务，在实际开发和设计中，需要考虑到在高并发下的服务限流、降级，针对不用的业务都有自己的限流策略。

常见的限流算法有：令牌桶、漏桶。计数器也可以进行粗暴限流实现。

## 令牌桶
- 每秒会有 r 个令牌放入桶中，或者说，每过 1/r 秒桶中增加一个令牌
- 桶中最多存放 b 个令牌，如果桶满了，新放入的令牌会被丢弃
- 当一个 n 字节的数据包到达时，消耗 n 个令牌，然后发送该数据包
- 如果桶中可用令牌小于 n，则该数据包将被缓存或丢弃

看完令牌桶的算法，有点类类似于分布式任务的抢占模式，定时将任务发布到令牌桶，对应执行的定时任务去获取令牌，获取到令牌了就执行任务，获取不到就执行熔断策略，当然这里的策略是终止任务。场景的可以用redis、Zookeeper去实现。

- 优点：流量比较平滑，入桶速率均匀，消费速率在一定时间内是匀速，不会出现流量出现很高的情况。

## 漏桶
- 固定大小的漏桶，流入速率任意，流程速率固定，为v
- 漏桶为空，不消费
- 流入速率任意，如果漏桶满了，抛弃
- 流出速率固定，为V

看完漏桶的算法，个人觉得有点类似于秒杀的设计，大量请求流入，先放到漏桶中（可以用队列或者redis，具体看项目需求和具体实现，当然也可以使用成熟的框架，纯属个人见解），然后按程序接受的速率均匀消费。

## 令牌桶和漏桶比较
- 1.令牌桶按一定速率往令牌桶中放入令牌，而获取令牌的速率由程序的获取能力来控制，控制的是流入的速率，消费的时候可以允许一定的并发数；
- 2.漏桶控制的是流出速率，按固定的速率流出流量，但是不可知流入速率，当流入的量达到漏桶的阈值时，阻止流量流入，平滑的控制流出速率；
- 3.两者控制的方向相反，令牌桶控制流入速率，流出速率可以有一定并发；漏桶控制的是流出速率，流入速率可以可以并发；且都有自己的容量；

## 实现
业界有许多实现方式，推荐2篇文章 
- [聊聊高并发系统之限流特技](https://m.baidu.com/from=2001a/bd_page_type=1/ssid=0/uid=0/pu=usm%401%2Csz%40320_1003%2Cta%40iphone_2_6.0_1_11.9/baiduid=692AA407E9D49D6C93E90F0F23454311/w=0_10_/t=iphone/l=3/tc?ref=www_iphone&lid=7466752170833192156&order=2&fm=alop&tj=www_normal_2_0_10_title&vit=osres&m=8&srd=1&cltj=cloud_title&asres=1&nt=wnor&title=%E8%81%8A%E8%81%8A%E9%AB%98%E5%B9%B6%E5%8F%91%E7%B3%BB%E7%BB%9F%E4%B9%8B%E9%99%90%E6%B5%81%E7%89%B9%E6%8A%80-%E4%BA%BF%E7%BA%A7%E6%B5%81%E9%87%8F%E7%BD%91%E7%AB%99%E6%9E%B6%E6%9E%84%E6%A0%B8%E5%BF%83...&dict=32&wd=&eqid=679f3b89f063a400100000005b075b28&w_qd=IlPT2AEptyoA_yiPHVWgohn1OVA7&tcplug=1&sec=30024&di=2edd490869e608c2&bdenc=1&tch=124.244.34.508.1.248&nsrc=IlPT2AEptyoA_yixCFOxXnANedT62v3IHBuPKyVZ0Cm7pEGshPPlGNVoHDbbNW_TXUL7uSPQpstRby3u0VAo7hJ3r_tj&clk_info=%7B%22srcid%22%3A1599%2C%22tplname%22%3A%22www_normal%22%2C%22t%22%3A1527208758925%2C%22xpath%22%3A%22div-a-h3%22%7D&sfOpen=1)
- [基于Redis的限流系统的设计](https://zhuanlan.zhihu.com/p/31285031)


这里就不对计数器做介绍了，真正理解了这几个概念，相信遇到具体场景的时候可以很快有解决方案，实现的工具也可以是多样性。


