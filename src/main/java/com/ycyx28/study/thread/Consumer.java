package com.ycyx28.study.thread;

public class Consumer implements Runnable{

	private String name;
	
	private Product product ;
	
	public Consumer(String name) {
		this.name = name;
		if(null == product){
			product = Product.getProduct();
		}
	}
	@Override
	public void run() {
		System.out.println(name+" 开始 消费产品...");
		while (true) {
			product.consume();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	
}
