package com.ycyx28.study.thread.execute;

import com.ycyx28.study.thread.Consumer;
import com.ycyx28.study.thread.Producer;

public class ProductExecute {

	public static void main(String[] args) {
		Producer producer = new Producer("张三");
		Thread thread = new Thread(producer);
		thread.start();
		
		Consumer consumer = new Consumer("李四");
		Thread thread2 = new Thread(consumer);
		thread2.start();
	}

}
