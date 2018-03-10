package com.ycyx28.study.thread;

import com.ycyx28.study.constant.Constant;

/**
 * 生产者
 * 
 * @author yc
 *
 */
public class Product {

	private static Integer MAX_PRODUCT = 10;

	private static Integer MIN_PRODUCT = 5;
	
	private static  Product  product= null;
	
	private Product(){
		
	}
	
	public static synchronized Product getProduct() {
		if(product == null ){
			product = new Product();
		}
		return product;
	}

	/**
	 * 生产者生产产品
	 */
	public synchronized void producer( ) {
		int num = Constant.pruductNum;
		if (num >= MAX_PRODUCT) {
			try {
				System.out.println("产品已满，请稍后再生产");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return ;
		}
		Constant.pruductNum++;
		System.out.println("生产者生产第" + Constant.pruductNum + "个产品了。");
		notifyAll();
	}

	/**
	 * 消费者消费产品
	 */
	public synchronized void consume() {
		int num = Constant.pruductNum;
		if (num <= MIN_PRODUCT) {
			try {
				System.err.println("缺货，稍候再取");
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		
		System.err.println("消费者取走了第" + num + "个产品");
		Constant.pruductNum -- ;
		notifyAll();
	}

}
