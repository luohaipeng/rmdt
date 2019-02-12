## 概述
&emsp;&emsp;该框架是基于消息队列的分布式事务解决方案（Reliable Message Distributed Transaction），框架名简称为RMDT。主要是为了解决分布式应用服务化后，事务不能保持一致性的问题。框架详情和架构设计可查看我的个人博客：
> http://blog.iloveyoubaby.online/space/java?currentPage=1&tag=%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1  
或者简书账号：LuoHaiPeng  
码云地址：https://gitee.com/luohiapeng/rmdt

## 技术选型
**开发工具**
- IDEA
- Maven
- Git  

**框架**
- SpringBoot
- Dubbo  
- Mybatis

**中间件**
- MySQL
- Zookeeper
- ActiveMQ

**其他工具**  
- Lombok
- Disruptor  

**补充说明**  
&emsp;&emsp;对于技术选型有几点需要说明的，第一点是：该框架直接使用Springboot构建，简化了我们构建项目和开发过程。同时使用了Spring IOC，并没有自己实现IOC，虽然这样会对Spring框架强依赖，但是我们要关注的核心是分布式事务，而不是IOC。  
&emsp;&emsp;第二点是：我们知道，市面上流行的分布式应用框架有很多，比如Dubbo、SpringCloud、Motan等，不同的框架有不同的实现细节，要让我们这个分布式事务框架支持市面上流行的分布式应用框架，那就必须做很多对应的适配工作，而我们时间有限，第一个版本先支持Dubbo，后续有时间再做扩展，或者大家可以贡献适配的代码。  
&emsp;&emsp;第三点是：Mybatis和Zookeeper并不是框架本身使用的，而是Demo项目使用的，框架本身操作关系型数据库没有依赖任何第三方ORM框架，而是直接使用JDBC操作。至于Zookeeper，就是Demo项目使用Dubbo搭建的一个分布式应用，服务的发现和注册使用Zookeeper中间件。  
&emsp;&emsp;第四点是：框架本身需要MySQL和ActiveMQ中间件支持，MySQL用于存储事务日志数据，ActiveMQ用于发送事务消息，但是框架内部并没有使用硬编码的方式集成这两个中间件，而是支持动态扩展，换句话说就是：存储事务日志的中间件可以通过配置的方式，切换为任意的存储技术，比如可以切换为Redis、MangoDB等。同理，消息中间件也是可以通过配置切换为常用的RabbitMQ、RocketMQ、Kafka等。具体如何配置看下文。
## 模块说明
&emsp;&emsp;整个项目包含以下几个子模块：rmdt-core，rmdt-common， rmdt-annotation，rmdt-dubbo，rmdt-demo。  
![项目模块](https://upload-images.jianshu.io/upload_images/10574922-135075f68fe9027f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
我们来看看每个模块的作用：  
- rmdt-core  
顾名思义，它就是存放核心代码的模块，整个项目所有处理流程和逻辑类都放到这里。
- rmdt-common  
存放一些各个子模块有可能会用到的公共文件，公共类和公共的配置。
- rmdt-annotation  
存放注解的模块，我们之前说了，要让别人用起来简单，最好贴个注解就能有对应的功能，所以我们涉及到的注解的定义都放到这里。但实际上，整个项目肯定不可能有很多注解，把这为数不多的注解放到一个模块中，最主要的目的是，别人在使用框架时，可以导入最少依赖。什么意思呢？举个例子：如果是使用Dubbo作为分布式框架，那么肯定会有API这样的子项目，API项目中放的是对外提供服务的接口类，那就有可能我们的注解就需要贴在这些接口类的抽象方法上，而这种API项目是不会有具体实现逻辑的，所以它能用的上的就只有注解，不会使用我们的core、common，这些模块中的类，那这样的话，这个API项目就只导入annotation这个模块的依赖就行了。所以，这就是为什么几个注解类，也要单独放一个模块。
- rmdt-dubbo  
我们在之前的设计稿中可以知道，调用远程RPC方法前，需要给RPC地址添加参数，但是每种具体的分布式应用框架传参都是不一样的，比如Dubbo和SpringCloud就有很大的区别，SpringCloud相对来说要简单很多，因为它就是一个RESTful资源路径而已，往该资源路径再追加一个参数很简单，而如果大家对Dubbo的源码有了解的话，就知道往RPC地址加参数，需要做比较多的事情。所以，为了后面框架的扩展，每一种分布式应用框架的支持，都单独创建一个模块，比如，现在我们框架需要支持Dubbo，那么就创建一个rmdt-dubbo的模块，用户存放处理远程方法调用和参数传递的类和文件。同理，如果对框架扩展，让它也能支持SpringCloud，就需要再创建一个rmdt-springcloud的模块。
- rmdt-demo  
该模块主要是用于方便测试功能的，其实就是我们经常说的业务项目了，它不是框架的一部分，但为了能让自己开发方便，和别人测试使用方便，就把demo放到了框架中。其中，我们可以看到，rmdt-demo模块下多了7个子模块，对Dubbo项目比较熟悉的小伙伴，应该不用过多的解析了，这7个模块都是业务类的项目，他们分别为：
- rmdt-demo-client  
作为服务消费者项目，也就是我们说的客户端。
- rmdt-demo-goods-api和rmdt-demo-goods-server  
共同构成商品系统项目，其中api为对外提供的服务接口，server为具体的服务实现。
- rmdt-demo-member-api和rmdt-demo-member-server  
共同构成会员系统项目，其中api为对外提供的服务接口，server为具体的服务实现。
- rmdt-demo-order-api和rmdt-demo-order-server  
共同构成订单系统项目，其中api为对外提供的服务接口，server为具体的服务实现。  
## 功能演示说明
&emsp;&emsp;我们可以运行框架中的demo项目，了解框架的功能效果。但在运行前，先简单解析一下这个demo项目的业务（注意：demo只是模拟业务需求，并发真实逻辑）：*客户端发起RPC请求，调用远程订单系统中的付款方法makePayment。在makePayment方法中有两个操作：分别是发送两个RPC请求，调用远程会员系统的付款方法payment，和远程商品系统的扣库存方法decrease*，在这个模拟的业务需求中，我们可以测试出分布式事务的问题，因为payment和decrease分别做付款和扣库存的操作，这两个操作是同一个事务的，要么两个都成功，要么两个都失败，但是由于现在的架构是分布式应用，他们各自都运行在自己的JVM中，这就不能确保事务一致性了。而使用了RMDT框架后，就能确保分布式事务的一致性了，**实现原理在之前的分析文章中已经讲过了，这里就不再提及**。那么接下来，我们来启动demo项目。
- 1、导入代码  
把GitHub上的该项目clone下来，导入到IDEA或者Eclipse（推荐使用IDEA），稍等片刻，让开发工具把环境build好。
- 2、导入测试数据  
项目clone下来后，在rmdt-demo模块中下有3个SQL文件：  
![](https://upload-images.jianshu.io/upload_images/10574922-9266fe706c909670.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
在自己本地MySQL服务创建3个数据库rmdt-demo-goods，rmdt-demo-member，rmdt-demo-order，分别是给商品系统，会员系统和订单系统使用，创建好这3个数据库后，把这3个SQL文件分别导入到以上3个数据库中：
![](https://upload-images.jianshu.io/upload_images/10574922-b3f16dbdf7f8f45d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
- 3、启动zookeeper  
由于使用到了zookeeper作为服务发现和注册中心，而配置文件中，连接的zookeeper地址是本地，所以需要在自己本地电脑启动zookeeper服务
![](https://upload-images.jianshu.io/upload_images/10574922-40a70c334d332878.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 4、启动ActiveMQ  
ActiveMQ用于发送事务消息，给框架做事务补偿提供保障。在配置文件中，也是连接本地ActiveMQ服务，所以需要在本地启动一个ActiveMQ。
![](https://upload-images.jianshu.io/upload_images/10574922-65e5750db06fc769.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 5、运行springboot程序  
分别把demo项目的4个springboot应用启动起来（直接运行main方法即可）：  
这个4个程序分别是rmdt-demo-client模块下的ClientApp类、rmdt-demo-goods-server模块下的GoodsApp类、rmdt-demo-member-server模块下的MemberApp类、rmdt-demo-order-server模块下的OrderApp类：
![](https://upload-images.jianshu.io/upload_images/10574922-93df139be59c466b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
- 6、发送请求  
使用postman测试demo项目模拟的业务方法。postman访问client应用中的/api/orders资源路径，设置的参数为count和price，也就是购买数量和商品价格，点击send按钮，发送请求：
![](https://upload-images.jianshu.io/upload_images/10574922-2cc2791e8486877f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
- 7、验证正常情况下的结果  
可以看到，请求成功，并且我们查看rmdt-demo-goods，rmdt-demo-member，rmdt-demo-order这3个数据库中的数据是有改变的，order_info表增加了一条订单，user_account表中的balance字段扣了1，inventory表中的total_inventory字段减了1。
![](https://upload-images.jianshu.io/upload_images/10574922-9ce5a0cfd6c999b3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
- 8、验证错误情况下的结果  
到这里，我们就把demo项目启动起来，并且可以正常执行模拟的业务流程了，以上执行的情况是没有出错的情况，那现在我们来演示其中一个远程服务出错的情况：把商品系统服务停掉，然后再执行postman的send请求，此时我们再查看数据，订单生成了，并且用户余额也扣了，而我们模拟商品系统宕机，导致库存扣减失败，这时就出现了数据不一致的情况了：
![](https://upload-images.jianshu.io/upload_images/10574922-6d81ce3c002945bb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
- 9、事务最终一致性  
通过第8步，我们已经模拟出分布式事务问题出来了，而要让改demo项目数据最终一致性很简单，只需要重新启动商品系统即可：
![](https://upload-images.jianshu.io/upload_images/10574922-597bc1a52c874a99.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
*注：这里只演示了某个服务宕机的情况。如果是服务没有宕机，但是执行业务方法过程中，由于各种原因导致抛出异常，导致数据不一致的情况，框架也是支持数据恢复一致性的，大家可以自行演示*
## 使用说明
&emsp;&emsp;功能效果刚刚已经看到了，那接下来看看如何使用。RMDT框架使用起来比较简单，我们还是通过内置的demo项目来看看框架的使用。
- 1、导入框架依赖  
在需要使用RMDT框架的项目导入框架的相关依赖，如Demo项目：rmdt-demo-member-server、rmdt-demo-goods-server、rmdt-demo-order-server这3个模块需要导入rmdt-dubbo依赖。rmdt-demo-member-api、rmdt-demo-goods-api、rmdt-demo-order-api这3个模块需要导入rmdt-annotation依赖。如下图：  
![](https://upload-images.jianshu.io/upload_images/10574922-d8831b4a137d6e0e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
由于api项目只用到注解，不需要使用具体功能逻辑，所以api项目只需要导入存放注解的rmdt-annotation依赖即可。这里以rmdt-demo-order-server和rmdt-demo-order-api模块举例，其他模块同理。
- 2、配置框架扫描的包  
在各个服务提供者系统中的SpringBoot主启动类，添加框架包的扫描范围：  
![](https://upload-images.jianshu.io/upload_images/10574922-365019bc9863d929.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
需要扫描的包路径如有多个则用英文逗号隔开。这里以订单系统举例，其他服务系统同理。
- 3、配置初始化参数  
在各个服务提供者系统中，创建Spring的Configuration配置对象，配置框架初始化参数：
![](https://upload-images.jianshu.io/upload_images/10574922-a0fbb55d08d47534.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
这里以订单系统举例，其他服务系统同理。
- 4、贴上分布式事务注解  
在需要开启分布式事务的业务方法上，贴上@Rmdt注解，抽象方法和实现方法都需要贴：  
![](https://upload-images.jianshu.io/upload_images/10574922-eac14c80c4eba117.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
通过上图可以发现，订单系统的makePayment方法上的@Rmdt注解是没有destination属性的，那是由于destination属性是用于发送事务消息的，订单系统的makePayment是事务发起者，不需要发送事务消息，所以不需要配置destination属性。
- 5、引用远程服务  
在Dubbo的@Reference注解中，添加一个proxy属性，属性值为"rmdtProxyFactory"，意思为使用RMDT框架自定义的代理工厂：  
![](https://upload-images.jianshu.io/upload_images/10574922-6be9ee25af414466.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
但这里需要注意的是：业务方法开启了分布式事务处理，才需要配置`proxy = "rmdtProxyFactory"`，否则不用配置还是使用Dubbo默认的代理工厂即可。
## 扩展说明
&emsp;&emsp;框架中的事务日志存储组件使用的是JDBC操作mysql，而MQ消息组件使用的是ActiveMQ，但框架中并没有硬编码的写死这些实现，而是以SPI的方式动态扩展，尽量做到黑盒扩展，而不是白盒修改。具体看以下扩展说明：
###  事务日志存储组件扩展  
1、创建一个配置Bean类，继承框架的**BaseRepositoryConfig**，给该类添加存储技术需要的属性，比如连接地址，用户名、密码和连接池大小等。  
2、创建一个类，实现事务日志存储组件SPI（**RmdtTransactionRepository**），该实现类需要自己写代码，实现数存储。  
3、然后在**resources/META-INF**目录下，创建**services**子目录，在services目录中新建名为**org.rmdt.core.repository.RmdtTransactionRepository**的文本文件。  
4、该文件内容为实现了RmdtTransactionRepository接口的类全限定名。  
5、在初始化配置类**RmdtConfig**中，设置repositoryName的值为实现类中，与**getRepositoryName**方法返回值相同的字符串。并且new出自定义的BaseRepositoryConfig子类配置对象，设置好RmdtTransactionRepository实现类需要的相关属性。  
具体操作如下：  
![](https://upload-images.jianshu.io/upload_images/10574922-bc8307833bc9bbef.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
### MQ消息组件扩展  
MQ消息组件扩展的方式与事务日志存储组件扩展的方式是一样的，这里就不详细说明了，类比学习即可。
## 架构说明
![总体执行流程](https://upload-images.jianshu.io/upload_images/10574922-8359450a1ad3988c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)    
流程图说明：    
- 图中左边淡蓝色区域为事务发起者执行流程中涉及到的接口和类，右边淡绿色区域为事务参与者执行流程中涉及到的接口和类，而中轴虚线上的接口和类是双方都需要的。
- 图中从上到下共分成9层，分别对应着Rmdt框架的7组件。其中Interceptor DubboProxy和Interceptor AOP同属Interceptor组件，Business不属于框架组件，而是框架使用者的业务类。
- 图中绿色小方块代表接口，蓝色小方块代表实现类，其中RmdtTransactionRepository和RmdtTransactionMessage为SPI，其他的都是API。
- 图中从黄色圆圈开始，蓝色虚线连接的地方是框架初始化时的操作，即启动时组装链，并且如果分布式应用框架使用的是Dubbo，那么Dubbo框架还有它自己的初始化操作，这部分在左上角蓝色虚线粗略描绘，详细的流程可以看Dubbo的源码解析文档。黑色实线为方法同步调用过程，深红色虚线为方法异步调用过程，即运行时调用链。线上的文字为调用的方法名。
