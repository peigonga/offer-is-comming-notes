## 设计模式

#### 设计原则
- 单一职责原则：规定一个类只有一个职责
- 开闭原则：规定软件中的对象(类、模块、函数等)对扩展开放，对修改关闭
- 里氏替换原则：规定任意父类可以出现的地方，子类都一定可以出现
- 依赖倒置原则：指称需要依赖于抽象，不依赖于具体实现
- 接口隔离原则：指将不同的功能定义在不同的接口中来实现接口的隔离，这样就
  避免了其他类在依赖该接口时依赖其不需要的功能
- 迪米特法则：指一个对象尽可能少的与其他对象发生相互作用，即一个对象对其他
  对象应该有尽可能少的了解或依赖
- 合成/聚合服用原则：指通过在一个新的对象中引入已有的对象以达到类的功能复用
  和扩展的目的。
  
#### 分类
按照功能和使用场景和氛围三大类：创建型(Creational Pattern)、
结构型(Structural Pattern)和行为型(Behavioral Pattern)

- 创建型：
    - 工厂模式(Factory Pattern)
    - 抽象工厂模式(Abstract Factory Pattern)
    - 单例模式(Singleton Pattern)
    - 建造者模式(Builder Pattern)
    - 原型模式(Prototype Pattern)
- 结构型：通过类和接口之间的继承和引用实现创建复杂结构对象的功能
    - 适配器模式(Adapter Pattern)
    - 桥梁模式(Bridge Pattern)
    - 过滤器模式(Filter Criteria Pattern)
    - 组合模式(Composite Pattern)
    - 装饰器模式(Decorator Pattern)
    - 外观模式(Facade Pattern)
    - 享元模式(Flyweight Pattern)
    - 代理模式(Proxy Pattern)
- 行为型：通过类之间不同的通信方式实现不同的行为
    - 责任链模式(Chain Of Responsibility Pattern)
    - 命令模式(Command Pattern)
    - 解释器模式(Interpreter Pattern)
    - 迭代器模式(Iterator Pattern)
    - 中介者模式(Mediator Pattern)
    - 备忘录模式(Memento Pattern)
    - 观察者模式(Observer Pattern)
    - 状态模式(State Pattern)
    - 策略模式(Strategy Pattern)
    - 模板模式(Template Pattern)
    - 访问者模式(Visitor Pattern)

