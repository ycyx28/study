package com.ycyx28.study.reflex;

public class ClassReflex {
	
	public static void main(String[] args) throws ClassNotFoundException {
		//1
		Student student = new Student();
		Class<?> stuClass1 = student.getClass();
		System.out.println(stuClass1.getName());
		
		//2
		Class<?> stuClass2 = Student.class;
		System.out.println(stuClass1 == stuClass2);
		
		//3
		Class<?> stuClass3 = Class.forName("com.ycyx28.study.reflex.Student");
		System.out.println(stuClass2 == stuClass3);
		
		
	}

}
