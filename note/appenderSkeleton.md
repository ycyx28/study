# dubbo日志追踪
在使用dubbo时，相信大家都遇到了很头疼的问题，就是服务提供者和服务消费者日志无法对应，如果有搭建自己的日志系统，在没有其他关键字的情况下，很难把服务消费者和服务提供者的日志对应起来，对线上排查问题造成了很大的阻碍，下面我分别从几个方面介绍下如何在dubbo服务端和消费者追踪日志。


## 1 AppenderSkeleton
AppenderSkeleton可以实现自定义log4j Appender ，只需要继承AppenderSkeleton重写期方法即可，下面介绍下AppenderSkeleton的基本方法，一般情况下只需要重写append方法即可

- 打印日志核心方法：abstract protected void append(LoggingEvent event); 
- 初始化加载资源：public void activateOptions()，默认实现为空
- 释放资源：public void close() 
- 是否需要按格式输出文本：public boolean requiresLayout() 

- [Log4自定义Appender](https://www.cnblogs.com/grh946/p/5977046.html)

# 2 MDC
MDC是为每个线程建立一个独立的存储空间，用threadlocal来保存每个线程的Hashtable的key/value信息。

MDC的put源码如下
``` java
 static public void put(String key, Object o) {
     if (mdc != null) {
         mdc.put0(key, o);
     }
  }
  
  private void put0(String key, Object o) {
    if(java1 || tlm == null) {
      return;
    } else {
      Hashtable ht = (Hashtable) ((ThreadLocalMap)tlm).get();
      if(ht == null) {
        ht = new Hashtable(HT_SIZE);
        ((ThreadLocalMap)tlm).set(ht);
      }    
      ht.put(key, o);
    }
  }
  
```

ThreadLocal的set源码
``` java
public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
    
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    
  private void set(ThreadLocal key, Object value) {


            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);

            for (Entry e = tab[i];e != null; e = tab[i = nextIndex(i, len)]) {
                ThreadLocal k = e.get();

                if (k == key) {
                    e.value = value;
                    return;
                }

                if (k == null) {
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }

            tab[i] = new Entry(key, value);
            int sz = ++size;
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }
        
```

由以上源码可知，MDC实际是依靠ThreadLocal实现，每个线下都有自己独立的值，线程安全。

## 3 Dubbo隐式参数
可以通过`RpcContext`上的`setAttachment`和`getAttachment`在服务消费方和提供之间进行参数的隐式传递

### 3.1 在服务消费方端设置隐式参数

setAttachment 设置的 KV 对，在完成下面一次远程调⽤会被清空，即多次远程调用要多次设置。

``` java
// 隐式传参，后面的远程调用都会隐式将这些参数发送到服务器端，类似cookie，用于框架集成，不建议常规业务使用
RpcContext.getContext().setAttachment("index", "1"); 
xxxService.xxx(); // 远程调用
//TODO 具体业务
```

### 3.2 在服务提供方端获取隐式参数
getAttachment 用过key获取setAttachment设置的value。

``` java
//获取客户端隐式传入的参数，用于框架集成，不建议常规业务使用
String index = RpcContext.getContext().getAttachment("index");
```

- 注意：path, group, version, dubbo, token, timeout 几个 key 是保留字段，请使用其它值

## 4 Dubbo Filter
服务提供方和服务消费方调用过程拦截，Dubbo 本身的大多功能均基于此扩展点实现，每次远程方法执行，该拦截都会被执行，需要注意对性能会有影响。

dubbo约定：
- 用户自定义 filter 默认在内置 filter 之后
- 特殊值 default，表示缺省扩展点插入的位置。比如：filter="xxx,default,yyy"，表示 xxx 在缺省 filter 之前，yyy 在缺省 filter 之后
- 特殊符号 -，表示剔除。比如：filter="-foo1"，剔除添加缺省扩展点 foo1。比如：filter="-default"，剔除添加所有缺省扩展点。
- provider 和 service 同时配置的 filter 时，累加所有 filter，而不是覆盖。

比如：
``` xml
<dubbo:provider filter="xxx,yyy"/> 
<!--和-->
<dubbo:service filter="aaa,bbb" />
<!--则 xxx,yyy,aaa,bbb 均会生效。如果要覆盖，需配置：-->
<dubbo:service filter="-xxx,-yyy,aaa,bbb" />
```

具体实现网上有很多线成代码，这里就不具体介绍，再实现具体日志追踪的时候再给代码。但是值得一提的是filter实现有很多种，可以配置不同的范围们，例如可以全局有效，可以只正对提供者或者消费者，或者配置有效。


## 结论
有了上面的介绍，相信怎么去上线Dubbo日志追踪应该有了一个大体的思路，接下来我将会提供具体实现代码。

