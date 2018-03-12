## HashMap

### HashMap的成员变量
- int DEFAULT_INITIAL_CAPACITY = 16：默认的初始容量为16 

- int MAXIMUM_CAPACITY = 1 << 30：最大的容量为 2 ^ 30 

- float DEFAULT_LOAD_FACTOR = 0.75f：默认的加载因子为 0.75f 

- Entry< K,V>[] table：Entry类型的数组，HashMap用这个来维护内部的数据结构，它的长度由容量决定 

- int size：HashMap的大小 

- int threshold：HashMap的极限容量，扩容临界点（容量和加载因子的乘积）

### HashMap的构造函数

- public HashMap()：构造一个具有默认初始容量 (16) 和默认加载因子 (0.75) 的空 HashMap 

- public HashMap(int initialCapacity)：构造一个带指定初始容量和默认加载因子 (0.75) 的空 HashMap 

- public HashMap(int initialCapacity, float loadFactor)：构造一个带指定初始容量和加载因子的空 HashMap 

- public HashMap(Map< ? extends K, ? extends V> m)：构造一个映射关系与指定 Map 相同的新 HashMap

### HashMap的结构

HashMap是一个“链表散列”,HashMap底层实现是数组，只是数组的每一项都是一条链。其中参数initialCapacity就代表了该数组的长度

``` java
static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;
        int hash;

        /**
         * Creates new entry.
         */
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }
```

### 计算该hash值在table中的下标

对于HashMap的table而言，数据分布需要均匀（最好每项都只有一个元素，这样就可以直接找到），不能太紧也不能太松，太紧会导致查询速度慢，太松则浪费空间。计算hash值后，怎么才能保证table元素分布均与呢？我们会想到取模，但是由于取模的消耗较大，而HashMap是通过&运算符（按位与操作）来实现的：h & (length-1)

``` java
static int indexFor(int h, int length) {
        return h & (length-1);
    }
```
### &运算介绍

按位“与”的计算是把两个数字分别写成二进制形式，然后按照每一位判断，&计算中，只要有一个是0就算成0

如：int a = 20&8

20->10100

8 ->01000

a = 00000

### addEntry方法介绍

``` java
void addEntry(int hash, K key, V value, int bucketIndex) {
        //如果size大于极限容量，将要进行重建内部数据结构操作，之后的容量是原来的两倍，并且重新设置hash值和hash值在table中的索引值
        if ((size >= threshold) && (null != table[bucketIndex])) {
            resize(2 * table.length);
            hash = (null != key) ? hash(key) : 0;
            bucketIndex = indexFor(hash, table.length);
        }
        //真正创建Entry节点的操作
        createEntry(hash, key, value, bucketIndex);
    }
```

``` java
   void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K,V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        size++;
    }
```

首先取得bucketIndex位置的Entry头结点，并创建新节点，把该新节点插入到链表中的头部，该新节点的next指针指向原来的头结点 
