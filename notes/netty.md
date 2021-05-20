## Netty

### 背景介绍

#### Netty是基于JDK NIO，为什么不直接用JDK NIO

###### Netty做的更多

- 支持常用应用层协议
- 解决传输问题：粘包、半包现象
- 支持流量整形
- 完善的断连、Idle等异常处理

###### Netty做的更好：

1. 规避JDK NIO bug
    - JDK epoll bug：linux 2.4时异常唤醒空转导致CPU 100%
    - IP_TOS参数（IP包优先级和QoS选项）使用时抛出异常
2. API更友好更强大
    - 如ByteBuffer 与Netty的ByteBuf
    - ThreadLoca与Netty的FastThreadLocal
3. 隔离变化，屏蔽细节
    - 隔离JDK NIO实现的变化：nio ->nio2(aio)
    - 屏蔽JDK NIO实现细节

#### Netty怎么切换三种IO模式

##### 三种IO模式

- BIO：阻塞IO
- NIO：非阻塞IO
- AIO：异步IO

###### 阻塞与非阻塞

数据没传输过来时，读会阻塞直到有数据；缓冲区满时，写操作也会阻塞。非阻塞遇到这些情况会直接返回

###### 同步与异步

数据就绪后需要自己去读是同步，数据就绪后直接读好再回调给程序是异步

###### Netty多种NIO实现

| COMMON                 | Linux                    | macOS/BSD                 |
| ---------------------- | ------------------------ | ------------------------- |
| NioEventLoopGroup      | EpollEventLoopGroup      | KQueueEventLoopGroup      |
| NioEventLoop           | EpollEventLoop           | KQueueEventLoop           |
| NioServerSocketChannel | EpollServerSocketChannel | KQueueServerSocketChannel |
| NioSocketChannel       | EpollSocketChannel       | KQueueSocketChannel       |

#### Netty如何支持三种Reactor

##### 什么是Reactor

Reactor是一种开发模式，模式的核心流程：

注册感兴趣的事件 -> 扫描是否有感兴趣的事件发生  -> 发生事件后做响应的处理

| client/server | SocketChannel/ServerSocketChannel | OP_ACCEPT | OP_CONNECT | OP_WRITE | OP_READ |
| ------------- | --------------------------------- | --------- | ---------- | -------- | ------- |
| client        | SocketChannel                     |           | Y          | Y        | Y       |
| server        | ServerSocketChannel               | Y         |            |          |         |
| server        | SocketChannel                     |           |            | Y        | Y       |

###### Thread-Per-Connection模式

```java
class Server implements Runnable {
  public void run(){
    try{
      ServerSocket ss = new ServerSocket(PORT);
      while(!Thread.interrupted()){
        new Thread(new Handler(ss.accept())).start();
      }catch(IOException e){
        
      }
    }
  }
  
  static class Handler implements Runnnable{
    final Socket socket;
    Handler(Socket s){
      socket = s;
    }
    
    public void run(){
      try{
        byte[] input = new byte[MAX_INPUT];
        socket.getInputStream().read(input);
        byte[] output = process(input);
        socket.getOutputStream().write();
      }catch(IOException e){
        
      }
    }
    
    private byte[] process(byte[] input){...};
  }
}
```

- Reacotr单线程模式
- Reactor多线程模式
- Reactor主从多线程模式

| 模式                    | 使用方式                                                     |
| ----------------------- | ------------------------------------------------------------ |
| Reactor单线程模式       | EventLoopGroup eventGroup = new NioEventLoopGroup(1);<br />ServerBootstrap serverBootstrap = new ServerBootstrap();<br /><br />serverBootstrap.group(eventGroup); |
| 非主从Reactor多线程模式 | EventLoopGroup eventGroup = new NioEventLoopGroup();<br />ServerBootstrap serverBootstrap = new ServerBootstrap();<br /><br />serverBootstrap.group(eventGroup); |
| 主从Reactor多线程模式   | //boss负责建立连接，worker负责处理具体的业务逻辑<br />EventLoopGroup bossGroup = new NioEventLoopGroup();<br />EventLoopGroup workerGroup = new NioEventLoopGroup();<br /><br />ServerBootstrap serverBootstrap = new ServerBootstrap();<br />serverBootstrap.group(bossGroup,workerGroup); |

###### Netty如何支持主从Reactor模式

将EventLoopGroup实例通过ServerBootstrap.group方法传入，之后在AbstractBootstrap.initAndRegister方法创建Channel并注册到EventLoopGroup中，具体是何种Channel通过ServerBootstrap.channel方法传入Channel.class最终通过泛型+反射创建。对于Server端，该Channel为ServerSocketChannel，并负责创建SocketChannel并交给workerEventLoopGroup管理。

ServerBootstrap在初始化时，最终会传入ServerBootstrapAcceptor这个接收器(也就是TCP连接建立的accept)，当进行read时，将传入的channel也就是SocketChannel注册给workerGroup进行管理

###### 为何说Netty的main reactor大多并不能用到一个线程组，只能用到线程组里的一个

当ServerBootstrap的初始化方法实际上是通过ServerBootstrap.bind方法调用的，该方法用来绑定IP地址和端口号，所以只用到了一个线程

###### Netty给Channel分配NIO Event Loop的规则是什么

EventLoopGroup在绑定Channel时，实际上使用过一个EventExecutorChooser分配具体的Executor，EventExecutorChooser是一个接口有两个具体实现，一个是GenericEventExecutorChooser，一个是PowerOfTwoEventExecutorChooser。两个实现中都有一个AtomicInteger计数器，前者是通过计数器自增然后对Executors数量取模再取绝对值获得一个Executor，后者是通过计数器自增后同Executor的数量-1做逻辑与，需要注意第二种Chooser只适用于Executor的数量为2的幂次方时才会生效

###### 通用模式的NIO实现多路复用器是怎么跨平台的

Netty通过SelectorProvider加载多路复用器，该类的provider方法会提供具体的provider实现。当property和ServiceLoader两种方式没有明确定义使用何种复用器是，通过DefaultSelectorProvider进行创建。DefaultSelectorProvider类中的create方法的跨平台实现，各平台的JVM返回自己平台的多路复用器实现，Win下为WindowsSelectorProvider，MacOS为KQueueSelectorProvider，Linux为EpollSelectorProvider

#### Netty对粘包、半包的解决方案

##### 什么是粘包和半包

当发送ABC、DEF两条消息是，对端不一定是这种格式，可能为ABCDEF一条消息全部收到，可能会是AB、CD、EF三条，对于前者就是粘包现象，后者是半包现象。

##### 为什么TCP应用中会出现粘包和半包现象

###### 粘包的主要原因

- 发送方每次写入数据 < 套接字缓冲区大小
- 接收方读取套接字缓冲区数据不够及时

###### 半包的主要原因

- 发送方写入数据 > 套接字缓冲区大小
- 发送的数据大于协议的MTU(Maximum Transmission Unit，最大传输单元)，必须拆包

换个角度看

- 收发：一个发送可能被多次接收，多次发送可能被一次接受
- 传输：一个发送可能占用多个传输包，多个发送可能会共用一个传输包

###### 究其根本其实是由于TCP是流式协议，消息无边界

而UDP像邮寄的包裹，虽然一次运输多个，但每个包裹都有界限，一个一个签收，所以无粘包、半包现象

##### 解决粘包和半包问题的几种常用方法

解决问题的根本手段：找出消息的边界

| 方式\比较                             | 方式\比较                                           | 寻找出边界的方式                                             | 优点                                       | 缺点                                                         | 推荐度 |
| ------------------------------------- | --------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------ | ------------------------------------------------------------ | ------ |
| TCP改成短连接<br />一个请求一个短连接 |                                                     | 建立连接到释放<br />连接之间的信息<br />即为传输信息         | 简单                                       | 效率低下                                                     | 不推荐 |
| 封装成帧                              | 固定长度                                            | 满足固定长度即可                                             | 简单                                       | 空间浪费                                                     | 不推荐 |
|                                       | 分隔符                                              | 分隔符之间                                                   | 空间不浪费<br />也比较简单                 | 内容本身出现分隔符时<br />需要转义，所以需要扫描内容         | 推荐   |
|                                       | 固定长度字段<br />代表内容长度<br />类似TCP头的形式 | 先解析固定长度<br />的字段获取内容<br />长度，然后读取<br />连续内容 | 精确定位应用数据，<br />内容也不需要转义   | 长度理论上有限制，<br />需提前预知可能的<br />最大长度从而定义长度占用字节数 | 推荐+  |
| 其他方式                              |                                                     | 每种都不同<br />例如JSON可以看{}是否已经成对                 | 衡量实际场景，<br />很多是对现有协议的支持 |                                                              |        |

##### Netty对三种常用封帧方式的支持

封装成帧

ByteToMessageDecoder(这个是Netty中的一次解码器)的三种实现

| 方式                       | 解码                         | 编码                 |
| -------------------------- | ---------------------------- | -------------------- |
| 固定长度                   | FixedLengthFrameDecoder      | 简单                 |
| 分隔符                     | DelimiterBasedFrameDecoder   | 简单                 |
| 固定长度字段存内容长度信息 | LengthFIeldBasedFrameDecoder | LengthFieldPrepender |



##### 解读Netty处理粘包、半包的源码

- 解码核心工作流程

  核心逻辑入口在ByteToMessageDecoder.channelRead方法，首先将读到的数据流存入数据积累器Cumulator中，然后将已积累到的数据返回传给相应的Decoder，根据Decoder的规则进行解码

- 解码中两种数据积累器(Cumulator)的区别

    - 一种是内存复制的方式：默认方式
    - 通过组合的方式，避免内存复制：效率要看使用何种Decoder

- 三种解码器的常用额外控制参数

    - FixedLengthFrameDecoder

      frameLength：用于控制固定帧的长度

    - DelimiterBasedFrameDecoder

      delimiters：分隔符，可以是多个字符

    - LengthFixedBasedFrameDecoder

        - lengthFieldOffset：指定应用报文长度属性的其实偏移量
        - lengthFieldLength：指定应用报文长度属性本身的长度
        - lengthAdjustment：
        - initialBytesToStrip：从第几字节开始解析内容

#### 常用的二次编解码方式

##### 为什么需要二次编解码

假设将解决半包粘包问题的解码器叫做一次解码器，实际在项目中，除了可选的压缩解压缩之外，还需要一层解码，因为一次解码的结果是字节，需要和项目中所使用的对象做转化，方便使用，这层解码器可以称为二次解码器，相应的，对应的编码器是为了将Java对象转换为字节流方便传输或存储

- 一次解码器：ByteToMessageDecoder

  io.netty.buffer.ByteBuf（原始数据流）  ->  io.netty.buffer.ByteBuf（用户数据）

- 二次解码器：MessageToMessageDecoder

  io.netty.buffer.ByteBuf（用户数据）   -> Java Object

可将一二次解码合并，但是不建议：

- 没有分层，不清晰
- 耦合性高，不容易置换方案

##### 常用的二次编解码方式

- Java序列化
- Marshaling
- XML
- JSON
- MessagePack
- ProtoBuf
- 其他

##### 选择编解码方式的要点

- 空间：编码后占用空间的大小
- 时间：编解码速度
- 是否追求可读性
- 多语言的支持

##### Protobuf简介与使用

Protobuf是一个灵活的、高效的用于序列化数据的协议。

相比XML和JSON，Protobuf更小、更快、更便捷

跨语言，并自带一个编译器protoc，只需要用它进行编译，可以自动生成Java、Python、C++等代码，不需要再写其他代码

##### Netty对二次编解码的支持



#### keepalive与idle监测

##### 为什么需要keepalive

用来检测连接确保连接正常且活跃，及时关闭不活跃或不可达的连接释放资源

##### 怎么设计keepalive？以TCP keepalive为例

- 问题出现概率小，没必要很频繁
- 判断需要谨慎，不能武断

```shell
# TCP keepalive核心参数
sysctl -a | grep tcp_keepalive
net.ipv4.tcp_keepalive_time = 7200
net.ipv4.tcp_keepalive_intvl = 75
net.ipv4.tcp_keepalive_probes = 9
# 当启用（默认关闭）keepalive时，TCP在连接没有数据通过的7200秒后发送keepalive消息，当探测没有确认时，按75秒的重试频率重发，一直发送9个探测包都没有确认，就认定连接失败
# 所以总耗时一般为：2小时11分（7200秒+75秒*9次）
```

##### 为什么还需要应用层keepalive

- 协议分层，各层关注点不同

  传输层关注是否通，应用层关注是否可服务

- TCP层的keepalive默认关闭，且经过路由等中转设备keepalive包可能被丢弃

- TCP层的keepalive时间太长

  默认大于2小时，虽然可改，但属于系统参数，改动影响所有应用

对于HTTP的Keep-Alive，指的是对长连接、短连接的选择

##### idle监测是什么

只是负责诊断，诊断后做出不同的行为，界定idle监测最终的用途

- 发送keepalive：一般用来配合keepalive，减少keepalive消息

  当没有消息传输时，超过一定时间判定为idle，之后在发送keepalive

- 直接关闭连接：

    - 快速释放损坏的、恶意的、很久不用的连接，让系统时刻保持最好的状态
    - 简单粗暴，客户端可能需要重连

##### 如何在Netty中开启TCP keepalive和idle检测

```java
//Server端开启TCP keepalive
bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
bootstrap.childOption(NioChannelOption.of(StandardSocketOptions.SO_KEEPALIVE),true);

//开启不同的idle check
ch.pipeline().addLast("idleCheckHandler",new IdleStateHandler(2,20,0,TimeUnit.SECONDS));
```

#### 源码解读Netty对TCP keepalive和三种idle检测的支持

##### 设置TCP keepalive怎么生效的

首先，使用childOption进行设置，child代表跟客户端做连接的SocketChannel，不是ServerSocketChannel

在ServerBootstrap在init时（由bind方法触发），会在Channel的pipeline中加入一个ServerBootstrapAcceptor（接受连接后的一个后续处理器），在连接建立后对连接进行设置

##### 两种设置keepalive的方式有什么区别

NioChannelOption最终是对JDK的Channel设置option

ChannelOption是对Socket设置

##### idle检测类包（io.netty.handler.timeout）的功能浏览

- IdleState：Idle状态的枚举
- IdleStateEvent：各状态对应的Event，共6个，分首次和非首次各两种
- IdleStateHandler：Idle检测的核心逻辑实现

##### 读idle的检测原理

ReaderIdleTimeoutTask内实现

##### 写idle检测原理和参数observeOutput用途

WriterIdleTImeoutTask内实现

observeOutput代表是否有写行为，比如正在写但缓冲区满，或有大文件正在写但未写完等

#### Netty中的锁

##### 分析同步问题的核心三要素

- 原子性
- 可见性
- 有序性

##### 锁的分类

- 对竞争的态度：乐观锁与悲观锁
- 等待锁的人是否公平而言：公平锁与非公平锁
- 是否可以共享：共享锁与独享锁，ReadWriteLock，其读锁是共享锁，其写锁是独享锁

##### Netty玩转锁的五个关键点

###### 在意锁的对象和范围：减少锁的粒度

例：初始化Channel（io.netty.bootstrap.ServerBootstrap#init）

###### 注意锁对象本身的大小：减少空间占用

例：统计待发送字节数（io.netty.channel.ChannelOutboundBuffer）

totalPendingSize使用volatile修饰，使用AtomicLongFieldUpdater进行修改

AtomicLong对比long：

前者是对象，包含对象头，以用来保存hashcode、lock等，32位系统占8字节，64位系统占16字节，所以64位系统下

- volatile long：8 bytes
- AtomicLong：8 bytes（volatile long） + 16bytes（对象头）+ 8bytes（引用） = 32bytes

###### 注意锁的速度：提高速度

记录内存分配字节数等功能用到的LongCounter（io.netty.util.internal.PlatformDependent#newLongCounter()）

高并发时，LongAdder性能优于AtomicLongCounter，所以netty及时使用JDK提供的新功能

###### 不同场景选用不同的并发类：因需而变

例：Nio Event Loop 中负责存储task的Queue

JDK的LinkedBlockingQueue（MPMC：多生产者多消费者模式） -> jctools的MPSC（多生产者但消费者模型）

###### 衡量好锁的价值：能不用就不用

Netty中一个线程负责一个EventLoop，每个EventLoop负责多个Channel，局部看是串行的，当多个Thread情况下，每个Thread负责各自的EventLoop，EventLoop负责各自的Channel，整体看是并行的

#### Netty对内存的使用

目标：

- 减少内存占用
- 应用速度快

对Java而言，减少FullGC的STW时间

##### 减少对象本身大小

1. 能用基本类型就不用包装类型

2. 应该定义成类变量的，不要定义成实例变量

3. Netty中结合前两者的实现：

   io.nety.channel.ChannelOutboundBuffer#incrementPendingOutboundBytes(long,boolean)方法中，longPendingSize为基本类型，AtomicLongFieldUpdater为类变量，所有实例共享一个

##### 对分配内存做预估

1. 对于已经可以预知固定size的HashMap避免扩容

   com.google.common.collect.Maps#newHashMapWithExpectedSize

2. Netty根据接收到的数据动态调整下个要分配的buffer的大小：可参考io.netty.channel.AdaptiveRecvByteBufAllocator

##### ZeroCopy

1. 使用逻辑组合代替实际复制

   如CompositeByteBuf：io.netty.handler.codec.ByteToMessageDecoder#COMPOSITE_CUMULATOR

2. 使用包装代替实际复制

   ```java
   byte[] bytes = data.getBytes();
   ByteBuf byteBuf = Unpooled.wrappendBuffer(bytes);
   ```

3. 调用JDK的Zero-Copy接口

   Netty中也通过在DefaultFileRegion中包装了NIO的FileChannel.transferTo()方法实现了零拷贝

##### 堆外内存

- 优点：
    - 破除堆空间限制，减轻GC压力
    - 避免复制
- 缺点
    - 创建速度满
    - 堆外内存受操作系统管理

##### 内存池

为什么引入对象池

- 创建对象开销大
- 对象高频率创建且可复用
- 支持并发又能保护系统
- 维护、共享有限的资源

Netty中轻量级对象池的实现：io.netty.util.Recycler

### 源码解读

#### 启动服务

###### 主线

- 用户线程：
    - 创建selector
    - 创建ServerSocketChannel
    - 初始化ServerSocketChannel
    - 给ServerSocketChannel从boss group中选择一个NioEventLoop
- boss线程：
    - 将ServerSocketChannel注册到NioEventLoop的selector
    - 绑定地址启动
    - 注册接受连接事件（OP_ACCEPT）到selector上

###### selector是在new NioEventLoopGroup时被创建的

NioEventLoopGroup构造方法中创建NioEventLoop后，由NioEventLoop的构造方法实现的

##### 启动服务的本质

```java
Selector selector = sun.nio.ch.SelectorProviderImpl.openSelector();

ServerSocketChannel serverSocketChannel = provider.openServerSocketChannel();

selectionKey = javaChannel().register(eventLoop().unwrappedSelector(),0,this);

javaChannel().bind(localAddress,config.getBacklog());

selectionKey.interestOps(OP_ACCEPT);
```

###### 知识点

- selector是在new NioEventLoopGroup()内创建一批NioEventLoop时创建的

- 第一次Register并不是监听OP_ACCEPT，而是0

  ```java
  selectionKey = javaChannel().register(eventLoop().unwrappedSelector(),0,this);
  ```

- 最终监听OP_ACCEPT是通过bind完成后的fireChannelActive()来触发的。

- NioEventLoop是通过register操作的执行来完成启动的

- 类似ChannelIninializer，一些handler可以设计成一次性的，用完即移除，例如授权

#### 构建连接

##### 主线

- boss thread
    - NioEventLoop中的selector轮询创建连接事件（OP_ACCEPT）
    - 创建socket channel
    - 初始化socket channel并从worker group中选择一个EventLoopGroup
- worker thread
    - 将socket channel注册到选择的NioEventLoop的selector
    - 注册读事件到（OP_READ）到selector

##### 知识点

- 接收连接的本质

  selector.select()/selectNow()/select(timeoutMillis)发现OP_ACCEPT事件，处理

    - SocketChannel socketChannel = serverSocketChannel().accept();
    - selectionKey = javaChannel().register(eventLoop()unwrappedSelector(),0,this);
    - selectionKey.interest(OP_READ);

- 创建连接的初始化和注册是通过pipeline.fireChannelRead在ServerBootstrapAcceptor中完成的。

- 第一次register并不是监听OP_READ，而是0：

  selectionKey = javaChannel().register(eventLoop().unwrappedSelector(),0,this);

- 最终监听OP_READ是通过Register完成后的fireChannelActive来触发的

- Worker's NioEventLoop是通过Register操作执行来启动

- 接受连接的读操作，不会尝试读取更多次（16次 ）

#### 接受数据

##### 读数据技巧

1. 自适应数据大小的分配器（AdaptiveRecvByteBufAllocator）：

   发放东西时，拿多大桶去装？小了不够，大了浪费，所以会自己根据实际装的情况猜测下次需要，从而决定下次带多大桶

2. 连续读（DefaultMaxMessagesPerRead）：

   发放东西时，假设拿的桶满了，这个时候你可能会觉得还有东西要发放，所以直接拿个新桶等着装，而不是回家，直到后面出现没有装上的情况或装了很多次需要给别人机会等原因才停止

##### 主线

- 多路复用器（Selector）接收到OP_READ事件
- 处理OP_READ事件：NioSocketChannel.NioSocketChannelUnsafe.read()
    - 分配一个初始1024字节的byte buffer来接收数据
    - 从Channel接收数据到byte buffer
    - 记录实际接收数据大小，调整下次分配byte buffer大小
    - 触发pipeline.fireChannelRead(byteBuf)把读取到的数据传播出去
    - 判断接收byte buffer是否满载而归：是，尝试继续读取到没有数据或达到16次；否，结束本轮读取，等待下次OP_READ事件

##### 知识点

- 读取数据的本质：sun.nio.ch.SocketChannelImpl#read(java.nio.ByteBuffer)

- NioSocketChannel read()是读数据，NioServerSocketChannel read()是创建连接

- pipeline.fireChannelReadComplete(); 一次读事件处理完成

  pipeline.fireChannelRead(byteBuf); 一次读数据完成，一次读事件处理可能包含多次读数据操作

- 为什么最多只尝试读取16次？“雨露均沾”

- AdaptiveRecvByteBufAllocator对byteBuf大小的猜测：放大果断，缩小谨慎（需要连续判断两次）

#### 业务处理

##### 主线

- pipeline的fireChannelRead处理业务逻辑

  pipeline中handler的执行资格：

    - 实现了ChannelInboundHandler
    - 实现方法channelRead且未加@Skip注解

###### 注意

inbound是从pipeline的head到tail执行

outbound是从pipeline的tail到head执行

##### 知识点

- 处理业务本质：数据在pipeline中所有handler的channelRead()执行过程

  Handler要实现io.netty.channel.ChannelInboundHandler#handleRead(ChannelHandlerContext ctx,Object msg)，而且不能加@Skip注解

  中途可退出，不保证执行到tail Hanlder

- 默认处理线程就是Channel绑定的NioEventLoop线程，也可以设置其他线程

  pipeline.addLast(new UnorderedThreadPoolEventExecutor(10),serverHandler);

#### 发送数据

##### 写数据的三种方式

| 快递场景（包裹）               | Netty写数据（数据）                       |
| ------------------------------ | ----------------------------------------- |
| 揽收到仓库                     | write：写入buffer                         |
| 从仓库发货                     | flush：把buffer里的数据发送出去           |
| 揽收到仓库并立马发货（加急件） | writeAndFlush：写到buffer马上发送         |
| 揽收与发货之间有个缓冲的仓库   | Write和Flush之间有个ChannelOutboundBuffer |

##### 写数据的要点

1. 对方仓库爆仓，送不了的时候，会停止送，协商等一个电话通知什么时候好了再送。

   Netty写数据，写不进去的时候会停止写，注册一个OP_WRITE事件，来通知什么时候可以写进去再写

2. 发送快递时，对方仓库都直接收下，这时候再发快递时，可以尝试发送更多的快递，这样效果更好

   Netty批量写数据时，如果想写的都写进去了，接下来可尝试写更多（调整maxBytesPerGatheringWrite）

3. 发送快递时，发到某个地方的快递特别多，我们会连续发送，但快递车毕竟有限，也会考虑其他地方

   Netty只要有数据要写，且能写的出去，则一直尝试，直到写不出去或满16次（WriteSpinCount）

4. 揽收太多，发送来不及时，爆仓，这个时候会出个告示牌：接受不了了，最好过两天再来邮寄

   Netty待写数据太多，超过一定的水位线（writeBufferWaterMark.high()），会将可写的标志位改成false，让应用端自己做决定要不要发送数据了

##### 主线

- Write：写数据到buffer

  ChannelOutboundBuffer#addMessage

- Flush：发送buffer里面的数据

  AbstractChannel.AbstractUnsafe#flush

    - 准备数据：ChannelOutboundBuffer#addFlush
    - 发送：NioSocketChannel#doWrite

##### 知识点

- 写的本质

    - Single Write：sun.nio.ch.SocketChannelImpl#write(java.nio.ByteBuffer)
    - Gathering Write：sun.nio.ch.SocketChannelImpl#write(java.nio.ByteBuffer[],int,int)

- 写数据写不进去时，会停止写，注册一个OP_WRITE事件，来通知什么时候可以写进去了

- OP_WRITE不是说有数据可写，而是说可以写进去，所以正常情况不能注册，否则一致触发

- 批量写数据时，如果尝试写的都写进去了，接下来会尝试写更多（maxBytesPerGatheringWrite）

- 只要有数据写，且能写，则一直尝试，直到16次（writeSpinCount），写16次还没有写完，就直接schedule一个task来继续写，而不是用注册写事件来触发，更简洁有力

- 待写数据太多，超过一定水位线（writeBufferWaterMark.high()），会将可写标志位置为false，让应用端自己做决定要不要继续写

- channelHandlerContext.channel().write()：从TailContext开始执行

  channelHandlerContext.write()：从当前Context开始

#### 断开连接

##### 主线

- 多路复用器（Selector）接收到OP_READ事件
- 处理OP_READ事件：NioSocketChannel.NioSocketChannelUnsafe.read()
    - 接收数据
    - 判断接收数据是否小于0，如果是，则说明是关闭开始执行关闭
        - 关闭channel（包含cancel多路复用器的key）
        - 清理消息：不接受新消息，fail掉所有queue中的消息
        - 触发fireChannelInactive和fireChannelUnregistered

##### 知识点

- 关闭连接的本质

  java.nio.channels.spi.AbstractInterruptibleChannel#close

  java.nio.channels.SelectionKey#cancel

- 要点

    - 关闭连接会触发OP_READ，读取字节数是-1代表关闭
    - 数据读取进行时，强行关闭触发IOException，进而执行关闭
    - Channel的关闭包含了SelectionKey的cancel

#### 关闭服务

##### 主线

- bossGroup.shutdownGracefully();

  workerGroup.shutdownGracefully();

  关闭所有group中的NioEventLoop：

    - 修改NioEventLoop的state标志位
    - NioEventLoop判断state执行退出

##### 知识点

- 关闭服务的本质
    - 关闭所有的连接及Selector
        - java.nio.channels.Selector#keys
            - java.nio.channels.spi.AbstractInterruptibleChannel#close
            - java.nio.channels.SelectionKey#cancel
        - selector.close();
    - 关闭所有线程：退出循环体for(;;)
- 关闭服务要点
    - 优雅（DEFAULT_SHUTDOWN_QUIET_PERIOD）
    - 可控（DEFAULT_SHUTDOWN_TIMEOUT）
    - 先不接活，然后尽量干完手头的事儿（先关boss，后关worker：不是100%保证）

### 易错点解析

- LengthFieldBasedFrameDecoder中initialBytesStrip未设置

- ChannelHandler顺序不正确

  pipeline中执行顺序为Inbound是由head至tail，Outbound是由tail至head

- ChannelHandler该共享不共享，不该共享却共享

- 分配ByteBuf：分配器直接用ByteBufAllocator.DEFAULT等，而不是采用ChannelHandlerContext.alloc()

- 未考虑ByteBuf的释放

- 错以为ChannelHandlerContext.write(msg)就写出数据了

- 乱用ChannelHandlerContext.channel().writeAndFlush(msg)

### 调优相关

#### 系统相关参数

##### SocketChannel

| Netty系统相关参数 | 功能                                                         | 默认值                                                       |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| SO_SNDBUF         | TCP数据发送缓冲区大小                                        | /proc/sys/net/ipv4/tcpwmem: 4K<br />[min,default,max]动态调整 |
| SO_RCVBUF         | TCP数据接收缓冲区大小                                        | /proc/sys/net/ipv4/tcp_rmem: 4K                              |
| SO_KEEPALIVE      | TCP层keepalive                                               | 默认关闭                                                     |
| SO_REUSEADDR      | 地址重用，解决Address already in use<br />常用开启场景：多网卡(IP)绑定相同关口；让关闭连接释放的端口更早可食用 | 默认不开启<br />澄清：不是让TCP绑定完全相同IP+PORT来重复启动 |
| SO_LINGER         | 关闭Socket的延迟时间，默认禁用，socket.close()方法立即返回   | 默认不开启                                                   |
| IP_TOS            | 设置IP头部的TypeOfService，用于描述IP包的优先级和QoS选项，例如倾向于延迟还是吞吐量 | 1000 - minimize delay<br />0100 - maximize throughput<br />0010 - maximize reliability<br />0001 - minimize monetary cost<br />0000 - normal service(默认) |
| TCP_NODELAY       | 设置是由启用NAGLE算法：用将小的碎片数据连接成更大的报文来提高发送效率 | FALSE<br />如果需要发送一些小的报文，则需要禁用该算法        |

##### ServerSocketChannel

| Netty相关参数 | 功能                                      | 备注                                                         |
| ------------- | ----------------------------------------- | ------------------------------------------------------------ |
| SO_RCVBUF     | 为accept创建的socket channel设置SO_RCVBUF | 为什么有SO_RCVBUF而没有SO_SNDBUF<br />该参数是为了保证SocketChannel创建出来后可立即生效，以防有ServerSocketChannel创建SocketChannel之后，还没有设置参数就接收到数据 |
| SO_REUSEADDR  | 是否可重用端口                            | 默认false                                                    |
| SO_BACKLOG    | 最大的半连接数                            | - 先尝试/proc/sys/net/core/somaxcon<br />- 再尝试sysctl<br />- 都没有使用128<br />使用方式：<br />javaChannel().bind(localAddress,config.getBacklog()); |
| IP_TOS        |                                           | 低版本jdk无效                                                |

##### 参数调整要点

- option/childOption要分清，设置错了不会报错，但不生效
- 可配置
- 不懂得不要动，避免过早优化

##### 需要调整的参数

- 最大打开文件数
- TCP_NODELAY、SO_BACKLOG、SO_REUSEADDR酌情设置

#### Netty核心参数

##### channelOption相关参数

| Netty参数                      | 功能                                                         | 默认值                                                       |
| ------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| WRITE_BUFFER_WATER_MARK        | 高低水位线、间接防止写数据OOM                                | 32k ~ 64k<br />连接级别参数，大量连接后加在一起会很大        |
| CONNECT_TIMEOUT_MILLIS         | 客户端连接服务器最大允许时间（等待时间）                     | 30秒                                                         |
| MAX_MESSAGES_PER_READ          | 最大允许连续读次数                                           | 16次                                                         |
| WRITE_SPIN_COUNT               | 最大允许连续写次数                                           | 16次                                                         |
| ALLOCATOR                      | ByteBuf分配器                                                | ByteBufAllocator.DEFAULT：大多池化、堆外                     |
| RCVBUF_ALLOCATOR               | 数据接收ByteBuf分配大小计算器+读次数控制器                   | AdaptiveRecvByteBufAllocator                                 |
| AUTO_READ                      | 是否监听读事件                                               | 默认：监听“读”事件：<br />设置此标记的方法也触发注册或移除读事件的监听 |
| AUTO_CLOSE                     | 写数据失败，是否关闭连接                                     | 默认打开，因为不关闭，下次还写，有可能还是失败               |
| MESSAGE_SIZE_ESTIMATOR         | 数据（ByteBuf、FileRegion）大小计算器                        | DefaultMessageSizeEstimator.DEFAULT<br />如计算ByteBuf:byteBuf.readableBytes() |
| SINGLE_EVENTEXECUTOR_PER_GROUP | 当增加一个handler且指定EventExecutorGroup时：决定这个Handler是否只用EventExecutorGroup中的一个固定的EventExecutor（取决于next()实现） | 默认：true<br />这个handler不管是否共享，绑定上唯一一个event executor，所以小名pinEventExecutor没有指定EventLoopGroup，复用channel的NioEventLoop |
| ALLOW_HALF_CLOSURE             | 关闭连接时允许半关                                           | 默认：不允许                                                 |
|                                |                                                              |                                                              |

###### ALLOCATOR与TCVBUF_ALLOCATOR

- 功能关联

  ALLOCATOR负责ByteBuf怎么分配（如从哪里分配），RCVBUF_ALLOCATOR负责计算为接收数据分配多少ByteBuf

  如：AdaptiveRecvByteBufAllocator有两大功能：

    1. 动态计算下次分配ByteBuf大小：guess()
    2. 判断是否可以继续读：continueReading

- 代码关联

  ```java
  io.netty.channel.AdaptiveRecvByteBufAllocator.HandleImpl handle = AdaptiveRecvByteBufAllocator.newHandle();
  ByteBuf byteBuf = handle.allocate(ByteBufAllocator)；
  //allocate实现
  ByteBuf allocate(ByteBufAllocator alloc){
    return alloc.ioBuffer(guess());
  }
  ```



#### System Property（ -Dio.netty.xxx，50+)

大概可分为三类

- 多种实现的切换
- 参数的调优
- 功能的开关

| Netty参数                                                    | 功能                        | 备注                                                         |
| ------------------------------------------------------------ | --------------------------- | ------------------------------------------------------------ |
| io.netty.eventLoopThreads                                    | IO Thread数量               | 默认：availableProcessors *2                                 |
| io.netty.availableProcessors                                 | 指定availableProcessors数量 | 考虑docket/VM等情况                                          |
| io.netty.allocator.type                                      | unpooled/pooled             | 池化还是非池化                                               |
| io.netty.noPreferDirect                                      | true/false                  | 堆内还是堆外                                                 |
| io.netty.noUnsafe                                            | true/false                  | 是否使用sum.misc.Unfase                                      |
| io.netty.leadDetection.level                                 | DISABLE/SIMPLE等            | 内存泄露检测级别，默认SIMPLE                                 |
| io.netty.native.workdir<br />io.netty.tmpdir                 | 临时目录                    | 从jar中解出native库存放的临时目录                            |
| io.netty.processId<br />io.netty.machineId                   | 进程号<br />机器硬件地址    | 计算channel的ID：<br />MACHINE_ID+PROCESS_ID+SEQUENCE+TIMESTAMP+RANDOM |
| io.netty.eventLoop.maxPendingTasks<br />io.netty.eventexecutor.maxPendingTasks | 存的task的最大数量          | 默认Integer.MAX_VALUE，显示设置为准，不低于16                |
| io.netty.handler.ssl.noOpenSsl                               | 关闭open ssl使用            | 优先open ssl                                                 |

##### 代码中设置的参数

```java
NioEventLoopGroup workerGroup = new NioEventLoopGroup();
workerGroup.setIoRatio(50);
//线程处理IO事件和业务Handler的比例
```

#### 如何让应用易诊断

##### 完善线程名

```java
NioEventLoopGroup boss = new NioEventLoopGroup(0,new DefaultThreadFactory("BOSS"));
NioEventLoopGroup worker = new NioEventLoopGroup(0,new DefaultThreadFactory("WORKER"));
```



##### 完善Handler名

```java
pipeline.addLast("FrameDecoder",new FrameDecoder());
```



##### 使用好Netty日志

#### Netty可视化监控

###### 统计并展示当先系统连接数

- Console定时输出
- JMX

##### Netty值得可视化的外在数据

| 可视化信息   | 来源                          | 备注                               |
| ------------ | ----------------------------- | ---------------------------------- |
| 连接信息统计 | channelActive/channelInactive |                                    |
| 收数据统计   | channelRead                   |                                    |
| 发数据统计   | write                         | ctx.write(msg).addListener()更准确 |
| 异常统计     | exceptionCaught/ChannelFuture | ReadTimeoutException.INSTANCE      |

##### Netty值得统计的内在数据

| 可视化信息      | 来源                                             | 备注                                         |
| --------------- | ------------------------------------------------ | -------------------------------------------- |
| 线程数          | 根据不同实现计算                                 | 如：<br />nioEventLoopGroup.executorCount(); |
| 待处理任务      | executor.pendingTasks();                         | 如：Nio Event Loop的待处理任务               |
| 积累的数据      | channelOutboundBuffer.totalPendingSize           | channel级别                                  |
| 可写状态切换    | channelWritablilityChanged                       |                                              |
| 触发事件统计    | userEventTriggered                               | IdleStateEvent                               |
| ByteBuf分配细节 | Pooled/UnpooledByteBufAllocator.DEFAULT.metric() |                                              |

#### Netty内存泄漏

##### Netty内存泄漏指的是什么

- 原因：没有release()

  ```java
  ByteBuf byteBuf = ctx.alloc().buffer();
  //buffer.release();
  //ReferenceCountUtil.release(buffer);
  ```

- 后果：OOM

    - 堆外：未free（PlatformDependent.freeDirectBuffer(buffer)）
    - 池化：未归还（recyclerHandle.recycle(this)）

##### Netty内存泄漏检测的核心思路

核心实现在ResourceLeakDetector中

引用计数（buffer.refCnt()） + 弱引用（WeakReference）

- 引用计数
- 弱引用

```java
ByteBuf buffer = ctx.alloc().buffer();
//引用计数+1，定义弱引用对象DefaultResourceLeak加到Set（allLeaks）里
buffer.release();
//引用计数-1，减到0时，自动执行释放资源操作，并将若引用从Set中移除

//判断依据：弱引用对象在不在Set中，如果在说明引用计数还没到0，说明没有执行释放
//判断时机：弱引用指向对象被回收时，可以把弱引用对象放入指定ReferenceQueue，所以遍历Queue拿出所有弱引用来判断
```

#### Netty自带注解

- @Sharable：标识Handler提醒可共享，不标记共享的不能重复加入pipeline
- @Skip：跳过Handler的执行
- @UnstableApi：提醒不稳定，慎用
- @SupressJava6Requirement
- @SupressForbiden

#### 优化线程模型

###### 业务两种常用场景

- CPU密集型：运算型
- IO密集型：等待型（操作等待的时间比CPU密集型要长）

###### CPU密集型

保持当先线程模型

- Runtime.getRuntime().availableProcessors() * 2
- io.netty.avaliableProcessors * 2
- io.netty.eventLoopThreads

###### IO密集型

独立出“线程池”来处理业务

- Handler内部使用JDK Executors

  ```java
  EventLoopGroup eventExecutorGroup = new UnorderedThreadPoolEventExecutor(10);
  pipeline.adLast(eventExecutorGroup,serverHandler);
  ```

#### 增强写，延迟与吞吐量的抉择

##### 问题：写全部采用加急方式

即使用ctx.writeAndFlush

- 改进方式1

  ```java
  //采用ChannelReadComplete
  public class EchoServerHandler extends ChannelInboundHandlerAdapter {
  
      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
          ctx.write(msg);
      }
  
      @Override
      public void channelReadComplete(ChannelHandlerContext ctx) {
          ctx.flush();
      }
  }
  ```



好处是一次读事件触发可能有多次读操作，所以这种方式是一次读事件只flush一次

缺点：

- 不适合异步业务线程（不复用NioEventLoop）处理：

  channelRead中的业务处理结果的write很可能发生在channelReadComplete之后

- 不适合更精细的控制：例如连续读16次时，第16次是flush，但是如果保持连续的次数不变，如何做到3次就flush

- 改进方式2：flushConsolidationHandler

  同步业务：read -> writeAndFlush  ->  readComplete

  异步业务：read  ->  readComplete  ->  writeAndFlush

#### 流量整形

###### 流量整形的用途

- 网盘限速（有意）
- 限流（无意）

##### Netty内置三种流量整形

- GlobalTrafficShapingHandler
- ChannelTrafficShapingHandler
- GlobalChannelTrafficShapingHandler

##### 源码总结

- 读写流控判断：按一定时间checkInterval（1s）来统计。writeLimit/readLimit设置值为0时，表示关闭读/写整形
- 等待时间范围控制：10ms   -> 15s
- 读流控：取消读事件监听，让读缓冲区满，然后对端写缓存区满，然后对端写不进去，对端对数据进行丢弃或减缓发送
- 写流控：待发数据如Queue。等待超过4s或单个channel缓存的数据超过4M 或所有缓存数据超过400M时修改写状态为不可写

#### 安全：设置高低水位线

##### Netty OOM的根本原因

- 根源：进（读速度）大于出（写速度）
- 表象：
    - 上游发送太快：任务重
    - 自己：处理慢/不发或发的慢 - 处理能力有限，流量控制等
    - 网速
    - 下游处理速度慢：导致不及时读取接收buffer数据，然后反馈到本端，发送速度降低

##### Netty OOM - ChannelOutboundBuffer

##### Netty OOM - TrafficShapingHandler

##### Netty OOM对策

- 设置好参数
    - 高低水位线（默认32K-64K）
    - 启用流量整形时需要考虑
        - maxwrite（默认4M）
        - maxGlobalWriteSize（默认400M）
        - maxWriteDelay（默认4s）
- 判断channel.isWritable()

#### 安全：启用空闲监控

```java
//服务端Idle检测
public class ServerIdleCheckHandler extends IdleStateHandler {

    public ServerIdleCheckHandler() {
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.info("idle check happened , close connection {}", ctx.channel().toString());
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}

bootstrap.handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioServerSocketChannel.class)
                .group(boss,worker)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast("idleCheck",new ServerIdleCheckHandler())
                                .addLast(new OrderFrameDecoder())
                                .addLast("FrameDecoder",new OrderFrameEncoder())
                                .addLast(new OrderProtocolEncoder())
                                .addLast(new OrderProtocolDecoder())
                                .addLast(orderBusinessPool,new OrderServerProcessHandler());
                    }
                });

//应用层keepalive
@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("[Keepalive] {}",ctx.channel().toString());
            KeepaliveOperation operation = new KeepaliveOperation();
            RequestMessage req = new RequestMessage(IdUtil.nextId(), operation);
            ctx.writeAndFlush(req);
        }
        super.userEventTriggered(ctx, evt);
    }
}

Bootstrap bootstrap = new Bootstrap();
        final KeepaliveHandler keepaliveHandler = new KeepaliveHandler();
        bootstrap.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup())
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new ClientIdleCheckHandler())
                                .addLast(new OrderFrameDecoder())
                                .addLast(new OrderFrameEncoder())
                                .addLast(new OrderProtocolEncoder())
                                .addLast(new OrderProtocolDecoder())
                                .addLast(new OperationToRequestMessageEncoder())
                                //keepalive是应用层实现，需要编解码，所以放到编解码之后
                                .addLast(keepaliveHandler)
                                .addLast(new LoggingHandler(LogLevel.INFO));
                    }
                });
```

#### 简单有效的黑白名单

##### Netty中的cidrPrefix

就是网络地址中的掩码，255.0.0.0/8中的/8

#### 自定义授权

```java
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        try {
            Operation operation = requestMessage.getMessageBody();
            if (operation instanceof AuthOperation) {
                AuthOperation authOperation = AuthOperation.class.cast(operation);
                AuthOperationResult execute = authOperation.execute();
                if (execute.isPassAuth()) {
                    log.info("[auth successfully] {}", ctx.channel().toString());
                } else {
                    log.error("[auth failed] {}", ctx.channel().toString());
                    ctx.close();
                }
            } else {
                log.error("[msg not expected] {}", ctx.channel().toString());
                ctx.close();
            }
        } catch (Exception e) {
           log.error("[auth exception] {}",ctx.channel().toString(),e);
            ctx.close();
        }finally {
            ctx.pipeline().remove(this);
        }
    }
}
```

#### SSL

```shell
keytool -import -keystore /Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/security/cacerts -file /var/folders/h9/gfsmnsbx0k30tn8qgh0by92c0000gn/T/keyutil_localhost_7333080901007710586.crt -alias netty -storepass changeit
```

