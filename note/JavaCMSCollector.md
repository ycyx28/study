# 内存模型与垃圾回收
初学java的时候，老师和我们介绍java是跨平台的语言，之所以能跨平台，因为有jvm。其实jvm帮我们做了很多事，在java中，jvm内置了垃圾回收机制，帮我们承担着对象的创建
和销毁工作，释放了开发的工作量，但是如果需要对系统做一些优化和深入研究，就必须了解jvm的内存模型和垃圾回收机制以及场景的垃圾回收器。

## 内存模型
java程序运行时数据会存放在运行时数据区，这个存储区域包括方法区（Method Area）、虚拟机栈(VM Stack)、本地方法栈（Native Method Stack）、堆（Heap）、
程序计数器（Program Counter Register）。

|名称 |特征 |作用 |配置 |异常 |
|- |-|-|-|-|
|栈区 |线程私有，使用一段连续的内存空间|存放局部变量表、操作栈、动态链接、方法出口|-Xss |StackOverflowError OutOfMemoryError|
|堆|线程共享，生命周期与虚拟机相同|保存对象实例|-Xms -Xmx -Xmn|OutOfMemoryError|
|程序计数器|线程私有、占用内存小|字节码行号|无|无|
|方法区|线程共享|存储类加载信息、常量、静态变量等|-XX:PermSize -XX:MaxPermSize|OutOfMemoryError|

- 栈区

  - 栈里面存放的是基本的数据类型和引用
  - 线程私有，生命周期与线程相同。每个方法执行的时候都会创建一个栈帧（stack frame）用于存放 局部变量表、操作栈、动态链接、方法出口。
  - 栈的大小可以通过-XSs设置，如果不足的话，会引起java.lang.StackOverflowError的异常。

- 堆

  - 堆里面则是存放各种对象实例。
  - 堆内存由-Xms指定，默认是物理内存的1/64；最大的内存由-Xmx指定，默认是物理内存的1/4。
  - 默认空余的堆内存小于40%时，就会增大，直到-Xmx设置的内存。具体的比例可以由-XX:MinHeapFreeRatio指定
  - 空余的内存大于70%时，就会减少内存，直到-Xms设置的大小。具体由-XX:MaxHeapFreeRatio指定。
  - 新生代，`-xmn` ；`-Xms`和`-Xmx`,堆内存大小;旧生带 = (`-Xms`)-(`-Xmn`)。
  
  一般情况下，`-Xms`和`-Xmx`设置大小一样，这样可以避免JVM频繁调整大小。
  
- 常见参数的解释
1. -Xms3g -Xmx3g -Xmn1g -XX:PermSize=64m -XX:MaxPermSize=128m
  
      `新生代分配1G，java heap最小3G，最大3G，持久带最小64M，最大128M，旧生带大小是-Xms减去-Xmn`
   
2. -XX:SurvivorRatio=8

      `新生代中Eden和S0的比值是8，对应上面配置，Eden大小是1024*0.8M，S0和S1分别是1024*0.1M`
      
3. -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:+CMSClassUnloadingEnabled

      `垃圾回收器使用CMS并发收集器，同时开启对旧生带的压缩，对于持久带区域也进行回收`

- 程序计数器

是一块较小的内存空间，记录了线程执行的字节码的行号，在分支、循环、跳转、异常、线程恢复等都依赖这个计数器。

- 方法区

保存类型信息、字段信息、方法信息、其他信息


