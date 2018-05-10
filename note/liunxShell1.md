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

### 语法
```shell
chmod [who] [+ | - | =] [mode] 文件名
```
#### 参数解释

操作对象who可是下述字母中的任一个或者它们的组合：
- u 表示“用户（user）”，即文件或目录的所有者。
- g 表示“同组（group）用户”，即与文件属主有相同组ID的所有用户。
- o 表示“其他（others）用户”。
- a 表示“所有（all）用户”。它是系统默认值。



