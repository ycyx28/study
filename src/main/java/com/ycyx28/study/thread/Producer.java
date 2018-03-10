package com.ycyx28.study.thread;

public class Producer implements Runnable{

	private String name;
	
	private Product product ;
	
	public Producer(String name) {
		this.name = name;
		if(null == product){
			product = Product.getProduct();
		}
	}
	@Override
	public void run() {
		System.out.println(name+" 开始生产产品...");
		while (true) {
			product.producer();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	
}
