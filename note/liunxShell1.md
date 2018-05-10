# Liunx常用命令
日常开发和学习中，会遇到很多shell命令，但是很多用过之后就忘了，下次使用的时候还得去百度或者Google，下面就记录下工作和学习中遇到的shell脚本。

## chown命令

将指定文件的拥有者改为指定的用户或组

### 语法

```shell
chown [-cfhvR] [--help] [--version] user[:group] file...
```
#### 参数解释
- user:新的文件拥有者的使用者 ID
- group : 新的文件拥有者的使用者群体(group)
- -c : 若该文件拥有者确实已经更改，才显示其更改动作
- -f : 若该文件拥有者无法被更改也不要显示错误讯息
- -h : 只对于连结(link)进行变更，而非该 link 真正指向的文件
- -v : 显示拥有者变更的详细资料
- -R : 对目前目录下的所有文件与子目录进行相同的拥有者变更(即以递回的方式逐个变更)，比较常用
- --help : 显示辅助说明
- --version : 显示版本

### 常用用法
创建group为`elsearch`，并同时创建用户`elsearch`,查看elasticsearch-5.6.3，属于root的group和root用户，通过chown修改文件的用户的拥有者为`elsearch`。
```shell
[root@localhost es]# groupadd elsearch
[root@localhost es]# useradd elsearch -g elsearch
[root@localhost es]# ll
total 4
drwxr-xr-x. 7 root root 4096 Oct  7  2017 elasticsearch-5.6.3
[root@localhost es]# chown -R elsearch:elsearch elasticsearch-5.6.3/
[root@localhost es]# ll
total 4
drwxr-xr-x. 7 elsearch elsearch 4096 Oct  7  2017 elasticsearch-5.6.3
```

## chmod 命令

改变文件或目录的访问权限，该命令有两种用法，一种是包含字母和操作符表达式的文字设定法；另一种是包含数字的数字设定法。

### 语法1-文字设定
```shell
chmod [who] [+ | - | =] [mode] 文件名
```
#### 参数解释

操作对象who可是下述字母中的任一个或者它们的组合：
- u 表示“用户（user）”，即文件或目录的所有者。
- g 表示“同组（group）用户”，即与文件属主有相同组ID的所有用户。
- o 表示“其他（others）用户”。
- a 表示“所有（all）用户”。它是系统默认值。

操作符号可以是：
- + 添加某个权限。
- - 取消某个权限。
- = 赋予给定权限并取消其他所有权限（如果有的话）。

设置mode所表示的权限可用下述字母的任意组合：
- r 可读
- w 可写
- x 可执行
- X 只有目标文件对某些用户是可执行的或该目标文件是目录时才追加x属性
- s 在文件执行时把进程的属主或组ID置为该文件的文件属主。方式“u＋s”设置文件的用户ID位，“g＋s”设置组ID位
- t 保存程序的文本到交换设备上
- u 与文件属主拥有一样的权限
- g 与和文件属主同组的用户拥有一样的权限
- o 与其他用户拥有一样的权限

### 语法2-数字设定
```shell
chmod [mode] 文件名
```

#### 参数解释
- 0表示没有权限，1表示可执行权限，2表示可写权限，4表示可读权限，然后将其相加
- 字属性的格式应为3个从0到7的八进制数，其顺序是（u）（g）（o）

### 常用用法
```shell
[root@localhost testfile]# ll
total 0
-rw-r--r--. 1 root root 0 May 10 15:52 aaa.txt
[root@localhost testfile]# chmod -R 777 aaa.txt 
[root@localhost testfile]# ll
total 0
-rwxrwxrwx. 1 root root 0 May 10 15:52 aaa.txt

[root@localhost testfile]# chmod -x aaa.txt 
[root@localhost testfile]# ll
total 0
-rw-rw-rw-. 1 root root 0 May 10 15:52 aaa.txt

[root@localhost testfile]# chmod -R +x aaa.txt 
[root@localhost testfile]# ll
total 0
-rwxrwxrwx. 1 root root 0 May 10 15:52 aaa.txt

```





