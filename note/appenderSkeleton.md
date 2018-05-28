# Log4自定义Appender
- [Log4自定义Appender](https://www.cnblogs.com/grh946/p/5977046.html)

## AppenderSkeleton
AppenderSkeleton可以实现自定义log4j Appender ，只需要继承AppenderSkeleton重写期方法即可，下面介绍下AppenderSkeleton的基本方法，一般情况下只需要重写append方法即可

- 打印日志核心方法：abstract protected void append(LoggingEvent event); 
- 初始化加载资源：public void activateOptions()，默认实现为空
- 释放资源：public void close() 
- 是否需要按格式输出文本：public boolean requiresLayout() 

# MDC
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

