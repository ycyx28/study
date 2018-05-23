# Java主线程等待子线程、线程池
在实际项目中，经常会遇到主线程需要等待子线程执行完后再执行的场景，在面试中也会经常会面到，在普通线程中用join就可以实现，但是在线程池中就显得比较吃力，
下面针对各种场景分别介绍下如何处理，如果有更好的处理方式或者见解，欢迎斧正。

## 主线程不等待子线程
首先，先介绍普通写法，主线程不等待子线程执行。

- ThreadDemo
``` java
package com.ycyx28.thread;

public class ThreadDemo implements Runnable {

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		System.out.println(Thread.currentThread().getName() + ":子线程开始执行");
		//Thread.yield( )方法，译为线程让步,它就会把自己CPU执行的时间让掉，让自己或者其它的线程运行
		Thread.yield();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println(Thread.currentThread().getName() + ":子线程执行结束，执行时间为："+ (end-start) + "ms" );
		
	}


}
```

- NomalThreadMain
``` java
package com.ycyx28.thread;

public class NomalThreadMain {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ThreadDemo demo = new ThreadDemo();
		for(int i = 0 ; i < 10 ; i ++) {
			Thread thread = new Thread(demo,"Thread-"+i);
			thread.start();
		}
		long end = System.currentTimeMillis();
		System.out.println("主线程执行完成，执行时间为："+ (end-start) + "ms" );

	}

}

```

- 执行结果
``` other
Thread-1:子线程开始执行
Thread-3:子线程开始执行
Thread-0:子线程开始执行
主线程执行完成，执行时间为：2ms
Thread-4:子线程开始执行
Thread-2:子线程开始执行
Thread-1:子线程执行结束，执行时间为：3001ms
Thread-4:子线程执行结束，执行时间为：3001ms
Thread-0:子线程执行结束，执行时间为：3001ms
Thread-2:子线程执行结束，执行时间为：3002ms
Thread-3:子线程执行结束，执行时间为：3001ms
```

## 主线程等待子线程
主线程等待子线程执行比较简单，使用join即可，下面贴上代码

- MainWaitThread
``` java
package com.ycyx28.thread;

import java.util.Vector;

public class MainWaitThread {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ThreadDemo demo = new ThreadDemo();
		Vector<Thread> vector = new Vector<Thread>();//控制并发，线程安全
		for(int i = 0 ; i < 5 ; i ++) {
			Thread thread = new Thread(demo,"Thread-"+i);
			thread.start();
			vector.add(thread);
		}
		
		for(Thread thread : vector) {
			try {
				thread.join();//join()的作用是：“等待该线程终止”
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("主线程执行完成，执行时间为："+ (end-start) + "ms" );

	}

}

```

- 执行结果
``` other
Thread-0:子线程开始执行
Thread-2:子线程开始执行
Thread-1:子线程开始执行
Thread-3:子线程开始执行
Thread-4:子线程开始执行
Thread-2:子线程执行结束，执行时间为：3001ms
Thread-0:子线程执行结束，执行时间为：3001ms
Thread-4:子线程执行结束，执行时间为：3001ms
Thread-1:子线程执行结束，执行时间为：3001ms
Thread-3:子线程执行结束，执行时间为：3001ms
主线程执行完成，执行时间为：3002ms
```

如果把join加在`thread.start()`下面，线程将会串行执行，下面看下执行效果。

``` java
package com.dinpay.demo.xml.thread;

public class MainWaitThread {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ThreadDemo demo = new ThreadDemo();
		for(int i = 0 ; i < 5 ; i ++) {
			Thread thread = new Thread(demo,"Thread-"+i);
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.println("主线程执行完成，执行时间为："+ (end-start) + "ms" );

	}

}

```

- 执行结果
``` other
Thread-0:子线程开始执行
Thread-0:子线程执行结束，执行时间为：3000ms
Thread-1:子线程开始执行
Thread-1:子线程执行结束，执行时间为：3000ms
Thread-2:子线程开始执行
Thread-2:子线程执行结束，执行时间为：3000ms
Thread-3:子线程开始执行
Thread-3:子线程执行结束，执行时间为：3001ms
Thread-4:子线程开始执行
Thread-4:子线程执行结束，执行时间为：3000ms
主线程执行完成，执行时间为：15003ms

```



