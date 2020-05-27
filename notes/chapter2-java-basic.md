## Java基础部分

### 集合
- List：有序集合
  - ArrayList：基于数组实现，增删慢，查询快，线程不安全
  - Vector：基于数组实现，增删慢，查询快，线程安全
  - LinkedList：基于双向链表实现，增删快，查询慢，线程不安全
- Queue：队列结构
  - ArrayBlockingQueue：基于数组结构实现的有界阻塞队列
  - LinkedBlockingQueue：基于链表结构实现的有界阻塞队列
  - PriorityBlockingQueue：支持优先级排序的无界阻塞队列
  - DelayQueue：支持延迟操作的无界阻塞队列
  - SynchronousQueue：用于线程同步的阻塞队列
  - LinkedTransferQueue：基于链表数据结构实现的无界阻塞队列
  - LinkedBlockingDeque：基于链表数据结构实现的双向阻塞队列
- Set：用于存储无序且不可重复的元素
  - HashSet：HashMap实现，无序
  - TreeSet：二叉树实现
  - LinkedHashSet：继承HashSet，HashMap实现数据存储，双向链表记录顺序
- Map：
  - HashMap：数组+链表存储数据，线程不安全，Java8之后数据结构优化为
    数字+链表或红黑树，链表中元素超过8各以后，链表结构转换为红黑树
  - ConcurrentHashMap：分段锁实现，线程安全。由多个Segment组成，
    每个Segment均继承ReentrantLock并单独加锁。每个Segment内部的
    数据结构和HashMap相同
  - HashTable：线程安全。遗留类，同一时刻只有一个线程能写，并发不如
    ConcurrentHashMap
  - TreeMap：继续二叉树的数据结构。
  - LinkedHashMap：继承HashMap，使用链表保存插入顺序
  
### 异常
- Throwable：是所有错误或异常的父类。可分为Error和Exception
  - Error：常见的Error由AWTError，ThreadDeath。
    出现Error通常是因为系统内部错误或资源耗尽，Error不能被在运行
    过程中被动态处理。
  - Exception：可分为RuntimeException和CheckedException
  
### 反射
Java中对象有两种类型：编译时类型和运行时类型
```java
Person person = new Student();
```
其中，person对象编译时类型为Person，运行时类型为Student，因此
无法在编译时获取在Student中定义的方法，编译期间无法预知该对象和
类的真实信息，只能通过运行时信息发现该对象和类的真实信息，而其真实
信息(对象的属性和方法)通常通过反射机制来获取，这边是反射机制的核心功能

##### 使用反射创建对象的两种方式
- 使用Class对象的newInstance方法，要求该Class对应的类有默认的孔构造器
- 使用Class获取指定的Constructor对象，在调用Constructor的newInstance
  方法创建

### 注解
元注解负责注解其他注解，分为：
- @Target：说明了注解所修饰的对象的范围，注解可被用于packages、types(
  类、接口、美剧、注解类型)、类型成员(方法、构造方法、成员变量、枚举值)、方法
  参数和本地变量(循环变量、catch参数等)
- @Retention：定义了该注解被保留的级别
  - SOURCE：在源文件中有效
  - CLASS：在Class文件中有效
  - RUNTIME：在运行时有效
- @Documented：表明这个注解应该被javadoc工具记录
- @Inherited：是一个标记注解，表明某个被标注的类型是被继承的


### 内部类
定义在类内部的类被称为内部类。可分为静态内部类、成员内部类、局部内部类和匿名内部类
- 静态内部类：静态内部类可以访问外部类的静态变量；在静态内部类中可以定义静态
  变量、方法、构造函数等；静态内部类通过"外部类.静态内部类"的方式来调用
- 成员内部类：定义在类内部的非静态类叫做成员内部类，在成员内部类中不能定义静态
  方法和变量(final修饰的除外)，因为成员内部类是非静态的，在Java中的非静态
  代码块中不能定义静态方法和变量
- 局部内部类：定义在方法中的类叫做局部内部类
- 匿名内部类：只通过继承一个父类或实现一个接口的方式直接定义并使用类

### 泛型
本质是参数化类型
标记：
- E-Element：表示集合中的元素
- T-Type：Java类
- K-Key：表示键
- V-Value：表示值
- N-Number：表示数值类型
- ？：表示不确定的Java类型

#### 类型擦除
在编码阶段使用泛型时加上的类型参数，会被编译器在编译时去掉，这个偶成就叫类型擦除。
因此反省主要用于编译阶段

### 序列化
