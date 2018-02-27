## HashMap
int DEFAULT_INITIAL_CAPACITY = 16：默认的初始容量为16 

int MAXIMUM_CAPACITY = 1 << 30：最大的容量为 2 ^ 30 

float DEFAULT_LOAD_FACTOR = 0.75f：默认的加载因子为 0.75f 

Entry< K,V>[] table：Entry类型的数组，HashMap用这个来维护内部的数据结构，它的长度由容量决定 

int size：HashMap的大小 

int threshold：HashMap的极限容量，扩容临界点（容量和加载因子的乘积）
