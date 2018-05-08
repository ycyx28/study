# Liunx文件字符串替换

## 一.获取文件名

当需要将文件夹中所有文件的`old_string`字符替换成`new_String`时，我们可以用批量替换的脚本来操作，首先，我们需要获取这些文件的文件名。
```shell
  grep 'old_String' -rl /usr/local/path 
```

- -r 选项表示递归(recursive)遍历所有子目录
- -l 选项表示只列出文件名
- /usr/local/path，文件目录

## 二.替换
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
