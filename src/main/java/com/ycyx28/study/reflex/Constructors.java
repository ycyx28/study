package com.ycyx28.study.reflex;

import java.lang.reflect.Constructor;

@SuppressWarnings({ "rawtypes", "unchecked","unused" })
public class Constructors {

	public static void main(String[] args) throws Exception {
		// 1.加载Class对象
		Class clazz = Class.forName("com.ycyx28.study.reflex.Student");

		// 2.获取所有公有构造方法
		System.out.println("**********************所有公有构造方法*********************************");

		Constructor[] constructors = clazz.getConstructors();

		for (Constructor c : constructors) {
			System.out.println(c);
		}

		System.out.println();
		System.out.println("************所有的构造方法(包括：私有、受保护、默认、公有)***************");
		constructors = clazz.getDeclaredConstructors();
		for (Constructor c : constructors) {
			System.out.println(c);
		}

		System.out.println();
		System.out.println("*****************获取公有、无参的构造方法*******************************");
		Constructor constructor = clazz.getConstructor();
		// 1>、因为是无参的构造方法所以类型是一个null,不写也可以：这里需要的是一个参数的类型，切记是类型
		// 2>、返回的是描述这个无参构造函数的类对象。

		System.out.println("constructor = " + constructor);

		// 调用构造方法
		Object obj = constructor.newInstance();

		System.out.println();
		System.out.println("******************获取私有构造方法，并调用*******************************");
		constructor = clazz.getDeclaredConstructor(char.class);
		System.out.println(constructor);
		// 调用构造方法
		constructor.setAccessible(true);// 暴力访问(忽略掉访问修饰符)
		obj = constructor.newInstance('男');

	}

}
