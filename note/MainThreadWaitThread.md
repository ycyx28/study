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
package com.ycyx28.thread;

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

## 主线程等待线程池
实际项目中多线程使用场景中，使用最多的还是使用线程池的情况，但是使用线程池又无法像多线程一样使用join来判断线程执行完毕，带着这个疑问查询了很多资料，网上推荐了2中解决办法(我只找到2个，如果有其他更好的，欢迎留言推荐)。

### awaitTermination
awaitTermination方法有2个参数，第一个是时间，第二个是时间单位，返回false即超时会继续循环，返回true即线程池中的线程执行完成主线程跳出循环往下执行，每隔传入时间循环一次 

``` java
package com.ycyx28.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainThreadWaitThreadPool {

	public static void main(String[] args) {
		 long start = System.currentTimeMillis();
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for(int i = 0 ; i < 10 ; i ++) {
			ThreadDemo demo = new ThreadDemo();
			pool.execute(demo);
		}
		pool.shutdown();
		
		try {
			//awaitTermination返回false即超时会继续循环，返回true即线程池中的线程执行完成主线程跳出循环往下执行，每隔10秒循环一次  
			while (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.println("wait pool execute...");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 

		long end = System.currentTimeMillis();  
		System.out.println("主线程执行完成，执行时间为："+ (end-start) + "ms" );
	}

}

```

- 执行结果

``` other
pool-1-thread-1:子线程开始执行
pool-1-thread-3:子线程开始执行
pool-1-thread-2:子线程开始执行
pool-1-thread-4:子线程开始执行
pool-1-thread-5:子线程开始执行
pool-1-thread-4:子线程执行结束，执行时间为：3000ms
pool-1-thread-2:子线程执行结束，执行时间为：3001ms
pool-1-thread-3:子线程执行结束，执行时间为：3001ms
pool-1-thread-4:子线程开始执行
pool-1-thread-5:子线程执行结束，执行时间为：3000ms
pool-1-thread-1:子线程执行结束，执行时间为：3001ms
pool-1-thread-5:子线程开始执行
pool-1-thread-3:子线程开始执行
pool-1-thread-2:子线程开始执行
pool-1-thread-1:子线程开始执行
pool-1-thread-3:子线程执行结束，执行时间为：3000ms
pool-1-thread-1:子线程执行结束，执行时间为：3000ms
pool-1-thread-2:子线程执行结束，执行时间为：3000ms
pool-1-thread-5:子线程执行结束，执行时间为：3000ms
pool-1-thread-4:子线程执行结束，执行时间为：3000ms
子线程执行时长：6004
```

### isTerminated
isTerminated也可以替代awaitTermination，isTerminated()是用来判断线程池是否执行完成，isTerminated执行比较耗性能，需要一直循环判断，但是可以增加休眠时间来达到awaitTermination同样的效果，下面贴上代码。

- MainThreadWaitThreadPool2
``` java
package com.ycyx28.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainThreadWaitThreadPool2 {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 10; i++) {
			ThreadDemo demo = new ThreadDemo();
			pool.execute(demo);
		}
		pool.shutdown();

		// awaitTermination返回false即超时会继续循环，返回true即线程池中的线程执行完成主线程跳出循环往下执行，每隔10秒循环一次
		while (!pool.isTerminated()) {
			try {
				System.out.println("will sleep 1s ,wait pool execute...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("主线程执行完成，执行时间为：" + (end - start) + "ms");
	}

}

```

- 执行结果

``` other
pool-1-thread-1:子线程开始执行
pool-1-thread-4:子线程开始执行
pool-1-thread-3:子线程开始执行
pool-1-thread-2:子线程开始执行
pool-1-thread-5:子线程开始执行
will sleep 1s ,wait pool execute...
will sleep 1s ,wait pool execute...
will sleep 1s ,wait pool execute...
pool-1-thread-5:子线程执行结束，执行时间为：3000ms
pool-1-thread-4:子线程执行结束，执行时间为：3000ms
pool-1-thread-2:子线程执行结束，执行时间为：3000ms
will sleep 1s ,wait pool execute...
pool-1-thread-3:子线程执行结束，执行时间为：3000ms
pool-1-thread-3:子线程开始执行
pool-1-thread-1:子线程执行结束，执行时间为：3000ms
pool-1-thread-2:子线程开始执行
pool-1-thread-4:子线程开始执行
pool-1-thread-5:子线程开始执行
pool-1-thread-1:子线程开始执行
will sleep 1s ,wait pool execute...
will sleep 1s ,wait pool execute...
will sleep 1s ,wait pool execute...
pool-1-thread-4:子线程执行结束，执行时间为：3001ms
pool-1-thread-2:子线程执行结束，执行时间为：3001ms
pool-1-thread-5:子线程执行结束，执行时间为：3001ms
pool-1-thread-1:子线程执行结束，执行时间为：3000ms
pool-1-thread-3:子线程执行结束，执行时间为：3000ms
主线程执行完成，执行时间为：7003ms
```

个人觉得`awaitTermination`比`isTerminated`好，不需要手动设置超时时间，超时重试，性能会更好。

## CountDownLatch实现主线程等待子线程
CountDownLatch可以把它看做一个倒数计数器，在引入包的时候发现它是java.util.concurrent中的一个辅助类，线程安全,下面具体看下实现代码。

- CountDownLatchThreadDemo.java
``` java
package com.dinpay.demo.xml.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchThreadDemo implements Runnable {

	private CountDownLatch countDownLatch;

	public CountDownLatchThreadDemo(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		try {
			long start = System.currentTimeMillis();
			System.out.println(Thread.currentThread().getName() + ":子线程开始执行");
			// Thread.yield( )方法，译为线程让步,它就会把自己CPU执行的时间让掉，让自己或者其它的线程运行
			Thread.yield();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();
			System.out.println(Thread.currentThread().getName() + ":子线程执行结束，执行时间为：" + (end - start) + "ms");
		}finally {
			//倒数器减1
			countDownLatch.countDown();
		}

	}

}

```

- CountDownLatchThreadMain.java

``` java
package com.dinpay.demo.xml.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchThreadMain {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		// 创建一个初始值为10的倒数计数器  
        CountDownLatch countDownLatch = new CountDownLatch(10); 
        CountDownLatchThreadDemo demo = new CountDownLatchThreadDemo(countDownLatch);
		for(int i = 0 ; i < 10 ; i ++) {
			Thread thread = new Thread(demo,"Thread-"+i);
			thread.start();
		}
		
		try {
			// 阻塞当前线程，直到倒数计数器倒数到0
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}  
		
		long end = System.currentTimeMillis();
		System.out.println("主线程执行完成，执行时间为："+ (end-start) + "ms" );

	}

}

```

- 程序运行结果

``` other
Thread-0:子线程开始执行
Thread-2:子线程开始执行
Thread-4:子线程开始执行
Thread-1:子线程开始执行
Thread-3:子线程开始执行
Thread-5:子线程开始执行
Thread-6:子线程开始执行
Thread-7:子线程开始执行
Thread-8:子线程开始执行
Thread-9:子线程开始执行
Thread-0:子线程执行结束，执行时间为：3000ms
Thread-6:子线程执行结束，执行时间为：3000ms
Thread-7:子线程执行结束，执行时间为：3000ms
Thread-4:子线程执行结束，执行时间为：3000ms
Thread-3:子线程执行结束，执行时间为：3000ms
Thread-2:子线程执行结束，执行时间为：3000ms
Thread-5:子线程执行结束，执行时间为：3000ms
Thread-8:子线程执行结束，执行时间为：3000ms
Thread-1:子线程执行结束，执行时间为：3000ms
Thread-9:子线程执行结束，执行时间为：3002ms
主线程执行完成，执行时间为：3004ms
```

另外，值得一提的是countDownLatch.await()中await()方法还有有参方法`public booleanawait(long timeout, TimeUnit unit)`，可以设置超时时间，返回是一个boolean的值，超时返回false，不超时返回true，无论返回结果是什么，将不会再等待子线程是否执行完，可以根据不同需求合理使用该方法，有兴趣的同学可以自己去试试，这里就不贴代码了。


