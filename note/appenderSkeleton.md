# Log4自定义Appender
- [Log4自定义Appender](https://www.cnblogs.com/grh946/p/5977046.html)

## AppenderSkeleton
AppenderSkeleton可以实现自定义log4j Appender ，只需要继承AppenderSkeleton重写期方法即可，下面介绍下AppenderSkeleton的基本方法，一般情况下只需要重写append方法即可

- 打印日志核心方法：abstract protected void append(LoggingEvent event); 
- 初始化加载资源：public void activateOptions()，默认实现为空
- 释放资源：public void close() 
- 是否需要按格式输出文本：public boolean requiresLayout() 
