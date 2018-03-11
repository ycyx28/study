# Java反射
- JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；
- 对于任意一个对象，都能够调用它的任意一个方法和属性；
- 这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制；
- 通俗的讲反射就是把java类中个种成分映射成一个个的java对象。

## 前置条件
必须先得到代表的字节码的Class，Class类用于表示.class文件

## 获取CLass对象的三种方式
- Object ——> getClass(); 如：(new Student()).getClass();
- 通过任何数据类型“静态”的class属性 ,如：Student.class;
- 通过Class类的静态方法：forName(String className)（常用）,其中className为包.类名。如：Class.forName("com.ycyx28.study.reflex.Student");

