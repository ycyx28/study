# Liunx文件字符串替换
当需要将文件的`old_string`字符替换成`new_String`时，我们有很多替换办法，下面根据不同替换情况介绍下替换的方法。

## 一.批量替换

  当需要将文件夹中所有文件的`old_string`字符替换成`new_String`时，我们可以用批量替换的脚本来操作
  
### 1.获取文件名

批量替换是，首先，我们需要获取这些文件的文件名。

```shell
  grep 'old_String' -rl /usr/local/path 
```

- -r 选项表示递归(recursive)遍历所有子目录
- -l 选项表示只列出文件名
- /usr/local/path，文件目录

### 2.批量替换

获取到文件名后，就可以执行替换操作了，下面给出替换脚本。

```shell
sed -i "s/old_string/new_String/g" `grep 'old_string' /usr/local/path`
```

或者单个文件替换

```shell
sed -i "s/old_string/new_String/g" /usr/local/path/demo.txt
```

- sed:删除、查找替换、添加、插入、从其他文件读取,这里用到的是替换
- -i 直接编辑文件选项
- s：替换指定字符
- g:使用后缀 /g 标记会替换每一行中的所有匹配

## 二.vi编辑替换

针对单个文件，我们可以通过vi命令编辑替换

1. vi编辑文件
2. 在非编辑模式下，输入`：%s/old_string/new_string/g`
3. 回车替换成功，其中`/g`标记会替换每一行中的所有匹配
4. `%s`替换开始


## 三.find替换

`find -name 'filename' | xargs perl -pi -e 's|old_String|new_String|g'` 或者 `find -P path -name 'filename' | xargs perl -pi -e 's\old_String\new_String\g'`

- `find -name 'filename' `可以在当前文件夹下搜索所有文件名为`filename`的文件路径，如:`find -name demo.txt`

- `find -P path -name filename`该命令可以指定在特定的文件夹内查找特定的文件,如：`find -P /usr/local/test -name demo.txt`,-P,表示path，路径

- `'s|old_String|new_String|g'` 替换，将"old_String"替换成"new_String",`/g`标记会替换每一行中的所有匹配.

- xargs,用作替换工具，读取输入数据重新格式化后输出,是给其他命令传递参数的一个过滤器，也是组合多个命令的一个工具

- perl ，Perl语言

- perl -pi -e 在Perl 命令中加上-e 选项，后跟一行代码，那它就会像运行一个普通的Perl 脚本那样运行该代码


## xargs
xargs命令是给其他命令传递参数的一个过滤器，也是组合多个命令的一个工具。它擅长将标准输入数据转换成命令行参数，xargs能够处理管道或者stdin并将其转换成特定命令的命令参数。xargs也可以将单行或多行文本输入转换为其他格式，例如多行变单行，单行变多行。xargs的默认命令是echo，空格是默认定界符。这意味着通过管道传递给xargs的输入将会包含换行和空白，不过通过xargs的处理，换行和空白将被空格取代。xargs是构建单行命令的重要组件之一。

- 定义一个demo.txt的测试文件
```shell
[root@localhost test]# cat demo.txt 
a b c d e f g
h i j k l m n 
o p q r s t 
u v w x y z 
```

- 多行变单行输出

```shell
[root@localhost test]# cat demo.txt | xargs
a b c d e f g h i j k l m n o p q r s t u v w x y z
```
- -n选择多行输出：-n3,表示一行显示3个字符串，默认空格作为分隔符号

```shell
[root@localhost test]# cat demo.txt | xargs -n3
a b c
d e f
g h i
j k l
m n o
p q r
s t u
v w x
y z
```
- -d选项可以自定义一个定界符
```shell
[root@localhost test]# echo "demo1#demo2#demo3" | xargs -d#
demo1 demo2 demo3

[root@localhost test]# echo "demo1##demo2##demo3" | xargs -d#
demo1  demo2  demo3

[root@localhost test]# echo "demo1#demo2#demo3" | xargs -d# -n2
demo1 demo2
demo3
```

## xargs结合find使用

- 查找删除文件

```shell
find -name demo.txt | xargs rm -f

find -P /usr/local/test -type f -name "*.txt" | xargs rm -rf

find . -type f -name "*.txt" | xargs rm -rf

find /usr/local/test -type f -name "*.txt" | xargs rm -rf

```

- 统计文件行数

```shell
[root@localhost test]# find . -type f -name "redis_master.conf" -print0 | xargs -0 wc -l
 23 ./testfile/redis_master.conf
 23 ./redis_master.conf
 46 total
 
 [root@localhost test]# find . -type f -name "redis_master.conf"  | xargs wc -l
 23 ./testfile/redis_master.conf
 23 ./redis_master.conf
 46 total

```

xargs -0将\0作为定界符

