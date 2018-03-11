# Java反射
- JAVA反射机制是在运行状态中，对于任意一个类，都能够知道这个类的所有属性和方法；
- 对于任意一个对象，都能够调用它的任意一个方法和属性；
- 这种动态获取的信息以及动态调用对象的方法的功能称为java语言的反射机制；
- 通俗的讲反射就是把java类中个种成分映射成一个个的java对象。

## 前置条件
必须先得到代表的字节码的Class，Class类用于表示.class文件

注意：Class对象是JVM自动创建，有且必须自从创建一个，一个类只产生一个class对象存入内存中，用“==”比较为true。

## 获取CLass对象的三种方式
- Object ——> getClass(); 如：(new Student()).getClass();
- 通过任何数据类型“静态”的class属性 ,如：Student.class;
- 通过Class类的静态方法：forName(String className)（常用）。其中className为包.类名。如：Class.forName("com.ycyx28.study.reflex.Student");

## 通过反射获取构造方法并使用
1. 获取构造方法
  - Constructor[] getContructors():获取所有“公有的”构造方法。
  - Constructor[] getDeclaredConstructors() : 获取所有的构造方法，包括私有的(private)、受保护的（protected）、默认的以及公有(public)的构造方法。
  - Constructor getConstructor(Class... parameterTypes):获取单个“公有(public)”的构造方法。
  - Constructor getDeclaredConstructor(Class... parameterTypes):获取某个构造方法，可以是私有的、受保护的、默认或者共有的构造方法。

2. 调用构造方法
  - Constructor ——>newInstance（Object... initargs）：newInstance是管理构造方法的类，返回值是T类型,所以newInstance是创建了一个构造方法的声明类的新实例对象，并为之调用。
  
## 通过反射获取成员变量并调用
1. 获取成员变量
  - Filed[] getFileds():获取所有的"公有(public)"字段。
  - Filed[] getDeclaredFileds()：获取所有字段，包括私有的(private)、受保护的（protected）、默认的以及公有(public)的字段。
  - Filed getFiled(String fileName):获取某个“公有的(public)”字段。
  - Filed getDeclaredFiled(String fileName):获取某个字段(可以是私有的)。
  
2. 设置字段的值
  - Filed ——>public void set(Object object, Object value): object：要设置的字段所在的对象；value： 要为字段设置的值。
  - Filed ——>public void setAccessible（boolean idPrivate）:idPrivate为true，暴力反射。接触私有限定，可以直接给私有变量赋值。
  
## 获取成员方法并调用
1. 获取成员方法
  - Method[] getMethods():获取所有“共有方法”；（包含父类的方法也包含Object类）。
  - Method[] getDeclareMethods():获取所有的成员方法，包括私有的（不包含继承的）。
  - Method getMethod(String name,Class<?>... parameterTypes)：获取公共方法。name，方法名；Class ...,形参的Class类型对象。
  - Method getDeclaredMethod(String name，Class<?>... parameterTypes)：获取方法（包括私有的）。name，方法名；Class ...,形参的Class类型对象。
  
2. 调用方法
  - Method ——>public Object invoke(Object obj,Object... args):obj,要调用的对象；args,调用方式时所传递的实参。






