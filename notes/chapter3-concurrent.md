## 并发

### 线程的创建方式
- 继承Thread
- 实现Runnable
- 通过ExecutorService和Callable<Class>实现有返回值的线程
- 基于线程池

##### 继承Thread
创建一个类继承Thread，然后实例化并调用start方法启动线程，start方法为native方法

##### 实现Runnable
实现Runnable，然后实例化后委托给Thread执行

##### 线程池
线程池的主要作用是线程复用、线程资源管理、控制操作系统最大并发数，以保证
系统高效(通过线程资源复用实现)且安全(通过控制最大线程并发数实现)的运行
由4个核心组件完成：
- 线程池管理器：用于创建并管理线程池
- 工作线程：线程池中执行具体任务的线程
- 任务接口：用于定义工作线程的调度和执行策略，只有线程实现了该解耦，线程中的
  任务才能被线程池调度
- 任务队列：存放待处理的人物，新的任务将会不断被加入队列中，执行完成的
  任务将被从队列中移除

##### 线程池工作流程
- 如果正在运行的线程数量少于corePoolSize，线程池就会立刻创建线程并执行
  该线程任务
- 如果正在运行的线程数量大于等于corePoolSize，该任务就将被放入阻塞队列中
- 在阻塞队列已满且正在运行的线程数量少于maximumPoolSize时，线程池会
  创建非核心线程立刻执行该任务
- 在阻塞队列已满且正在运行的线程数量大于等于maximumPoolSize，线程池将
  拒绝执行该线程任务并抛出RejectExecutionException异常
- 在线程任务执行完毕后，该任务将被从线程池队列中移除，线程池姜葱队列中
  取下一个线程任务继续执行
- 在线程处于空闲状态的时间超过keepAliveTime，正在运行的线程数量超过
  corePoolSize，该线程将会被认定为空闲线程并停止。

##### 线程池的拒绝策略
若线程池中的核心线程数被用完且阻塞队列已满，则此时线程池的资源耗尽，线程池没有
足够的线程资源执行新的任务。为了保证操作系统的安全，线程池将通过剧角色略处理
新添加的线程任务。JDK内置四种拒绝策略：
- AbortPolicy：直接抛出异常，阻止线程正常运行
  ```java
      public static class AbortPolicy implements RejectedExecutionHandler {
              
              public AbortPolicy() { }
  
              public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                  throw new RejectedExecutionException("Task " + r.toString() +
                                                       " rejected from " +
                                                       e.toString());
              }
          }
  ```
- CallerRunsPolicy:如果被丢弃的线程任务未关闭，则执行该线程任务
  ```java
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
            
          public CallerRunsPolicy() { }
    
          public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                if (!e.isShutdown()) {
                    r.run();
                }
            }
        }
  ```
- DiscardOldestPolicy:移除线程队列中最早的一个线程任务，并尝试提交当前任务
  ```java
    public static class DiscardPolicy implements RejectedExecutionHandler {
            
            public DiscardPolicy() { }
    
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                if (!e.isShutdown()) {
                    e.getQueue().poll();
                    e.execute(r);
                }
            }
        }
  ```
- DiscardPolicy：丢弃当前线程任务而不做任务和处理
  ```java
      public static class DiscardPolicy implements RejectedExecutionHandler {
              
              public DiscardPolicy() { }
      
              public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
              }
          }
  ```

- 另外还可以自行扩展RejectedExecutionHandler实现拒绝策略

##### 5种常用线程池
- newCachedThreadPool:可缓存的线程池
- newFixedThreadPool：固定大小的线程池
- newScheduledThreadPool：可做任务调度的线程池
- newSingleThreadExecutor：单线程线程池
- newWorkStealingPool：足够大小的线程池，1.8新增

### 锁
从悲观乐观角度可分为悲观锁和乐观锁，从获取资源的公平性上可分为公平锁和非公平锁，
从是否共享资源的角度可分为共享锁和独占锁，从锁的状态的角度可分为偏向锁、轻量级锁
和重量级锁

- 乐观锁：在每次读取数据时都认为别人不会修改数据，所以不会加锁，但是在更新时
  会判断数据有没有被更改，通常在写时先读出当前版本号然后加锁的方法
- 悲观锁：在每次读取数据时都认为别人会修改数据，所以每次读写数据时都会加锁

#### synchronized和ReentrantLock比较
##### 共同点：
- 都用于控制多线程对共享对象的访问
- 都是可重入锁
- 都保证了可见性和互斥性
##### 不同点
- ReentrantLock显示获取和释放锁；synchronized隐式获取和释放锁。
  为了避免程序出现异常无法正常释放锁，在使用ReentrantLock时必须在finally语句
  块中执行释放锁的操作
- ReentrantLock可响应中断、可轮回，为处理锁提供了更多的灵活性
- ReentrantLock是API级别的，synchronized是JVM级别的
- ReentrantLock可以定义公平锁
- ReentrantLock通过Condition可以绑定多个条件
- 二者底层实现不一样：synchronized是同步阻塞，采用的是悲观并发策略；
  Lock是同步阻塞，采用的是乐观并发策略
- Lock是一个接口，而synchronized是Java中的关键字，synchronized是由内置
  的语言实现的
- 通过Lock可以知道有没有成功获取锁，通过synchronized无法做到
- Lock可以通过分别定义读写锁提高多个线程读操作的效率

##### 如何进行锁优化
- 减少锁持有的时间：指只在有线程安全要求的程序上加锁来尽量减少同步代码块对
  对锁的持有时间
- 减小锁的粒度：指将单个耗时较多的锁操作拆分为多个耗时较少的锁操作来增加
  锁的并发度，减少同一个锁上的竞争。减少锁的竞争后，偏向锁、轻量级锁
  的使用率才会提高。如ConcurrentHashMap
- 锁分离：指根据不同的应用场景将锁的功能进行分离，以应对不同的变化，最常见的
  就是读写锁，这样读读不互斥，读写互斥，写写互斥
- 锁粗化：指为了保障性能，会要求尽可能将锁的操作细化以减少线程持有锁的时间，
  但是如果锁分的太细，将会导致系统频繁获取锁和释放锁，反而影响性能的提升。
  在这种情况下，建议将关联性强的锁操作集中起来处理，以提高系统的整体效率
- 锁消除：在开发中经常会出现不需要使用锁操作的情况下误用了所操作而引起了
  性能下降，这多数是因为编程不规范引起的
  
#### 阻塞队列
1. ArrayBlockingQueue：<br>
  基于数组实现的有界阻塞队列。按照先进先出的原则对元素进行排序，在默认情况
  下不保证元素操作的公平性。操作公平性指在生产者或消费者线程发生阻塞后再次
  被唤醒时，按照阻塞的先后顺序操作队列，即先阻塞的可以先插入或获取元素。保
  证公平性会降低吞吐量，所以如果处理的数据没有先后顺序，则可食用非公平的处
  理方式
2. LinkedBlockingQueue：<br>
  基于链表实现的阻塞队列，同ArrayBlockingQueue类似，此队列按照先进先出
  原则对元素进行排序；LinkedBlockingQueue对生产者端和消费者端分别采用
  两个独立的锁来控制数据同步，可以将队头的锁理解为写锁，队尾的锁理解为读锁，
  因此生产者和消费者可以基于各自独立的锁并行的操作队列中的数据，
  LinkedBlockingQueue的并发性能较高。
3. PriorityBlockingQueue：<br>
  支持优先级的无界队列，元素默认采用自然顺序升序排列。可自定义元素实现
  Comparable或初始化队列的时候指定Comparator来实现排序。
4. DelayQueue：<br>
  支持延迟获取元素的队列，底层使用PriorityQueue实现。DelayQueue中元素
  必须实现Delayed接口，该接口定义了再创建元素时该元素的延迟时间，在内部
  通过为每个元素的操作加锁来保障数据的一致性。只有在延迟时间到了之后才能从
  队列中提取元素。可以用于以下场景：
  - 缓存系统设计：用DelayQueue保存元素的有效期，使用一个线程循环查询
    DelayQueue，一旦能从DelayQueue中获取元素，则表示缓存元素的有效期到了
  - 定时任务调度：使用DelayQueue保存即将开始执行的任务和执行时间，一旦从
    DelayQueue中取得元素，就表示任务开始执行，Java中的TimerQueue就是使用
    DelayQueue实现的。
  demo:com.peigong.offer.chapter3_concurrent.queue.delayqueue
5. SynchronousQueue：<br>
  一个不存储元素的阻塞队列。SynchronousQueue中的每个put操作都必须等待
  一个take操作完成，否则不能继续向队列中添加元素。看作一个快递员，他负责把
  成产这县城的数据直接传递给消费者线程，非常适合用于传递场景。吞吐量高于
  LinkedBlockingQueue和ArrayBlockingQueue
6. LinkedTransferQueue：<br>
  基于链表结构实现的无界阻塞TransferQueue队列。
7. LinkedBlockingDeque：<br>
  基于链表结构实现的双向阻塞队列。可在队列两端分别执行插入和移除操作，这样
  在多线程同时操作队列时，可以减少一半的锁资源竞争，提高队列的操作效率
  
##### CountDownLatch
同步工具类，允许一个或多个线程一起等待其他线程的操作执行完后再执行相关操作。
```java
public class CountDownLatchDemo {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(() -> {
            try {
                System.out.println("子线程1执行");
                Thread.sleep(3000);
                System.out.println("子线程1执行完成");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                System.out.println("子线程2执行");
                Thread.sleep(3000);
                System.out.println("子线程2执行完成");
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        try {
            System.out.println("等待两个子线程执行完成");
            latch.await();
            System.out.println("全部子线程执行完成，主线程继续");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

##### CyclicBarrier
循环屏障是一个同步工具，可以实现让一组线程等待至某个状态之后再全部同时执行。
在所有等待线程全部被释放之后，CyclicBarrier可以被重用。
```java
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int n = 4;
        CyclicBarrier barrier = new CyclicBarrier(n);
        for (int i = 0; i < n; i++) {
            new Thread(new Business(barrier)).start();
        }
    }

    static class Business implements Runnable{

        private CyclicBarrier barrier;

        public Business(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {

                Thread.sleep(new Random().nextInt(6000));
                System.out.println("线程执行前准备工作完成，等待");
                barrier.await();
                System.out.println("全部就绪，全部完成");
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
```

##### Semaphore
指信号量，用于控制同时访问某些资源的线程个数，通过调用acquire()获取一个许可，
如果没有许可则等待，在许可使用完之后通过release()释放该许可。常被用于多个线程
需要共享有限资源的情况。
```java
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i < 5; i++) {
            new Thread(new Worker("worker"+i,semaphore)).start();
        }
    }

    static class Worker implements Runnable {

        private Semaphore semaphore;
        private String name;

        public Worker(String name,Semaphore semaphore) {
            this.name = name;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println(name + " using printer");
                Thread.sleep(2000);
                System.out.println(name + " done");
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

##### CountDownLatch、CyclicBarrier和Semaphore区别
- CountDownLatch和CyclicBarrier都用于实现多线程之间的互相等待，但二者
  的关注点不同。CountDownLatch主要用于主线程等待其他子线程任务均执行完毕
  后再执行接下来的业务逻辑单元，而CyclicBarrier主要用于一组线程互相等待
  大家都到达某个状态后，再同时执行接下来的业务逻辑单元。此外，CountDownLatch
  是不可重用的，CyclicBarrier是可重用的
- Semaphore和Java中的锁功能类似，主要用于控制资源的并发访问

#### volatile关键字
比synchronized的同步机制稍弱。也用于确保将变量的更新操作通知到其他线程。
volatile具备两种特性：
- 保证该变量对所有线程可见，在一个线程修改了变量的值后，新的值对于其他
  线程是可以立即获取的
- volatile禁止指令重排，即volatile变量不会被缓存在寄存器中或者对其他
  处理器不可见的地方，因此在读取volatile类型的变量时总会返回最新写入的值
<br>
volatile主要适用于一个变量被多个线程共享，多个线程均可针对这个变量
执行赋值或读取操作。使用volatile必须满足两个条件才能保证冰花环境
的线程安全：
- 对变量的写操作不依赖与当前值(i++)，或者说是单纯的变量赋值
- 该变量没有被包含在具有其他变量的不变式中，也就是说在不同的volatile
  变量之间不能相互依赖，只有在状态真正独立于程序内的其他内容时才能使用
  volatile。