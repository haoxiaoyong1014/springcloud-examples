### hystrix-provider-service

### 熔断器

#### 服务雪崩

服务雪崩效应是一种因 `服务提供者` 的不可用导致 `服务调用者` 的不可用,并将不可用 逐渐放大 的过程;

下图中, A为服务提供者, B为A的服务调用者, C和D是B的服务调用者. 当A的不可用,引起B的不可用,并将不可用逐渐放大C和D时, 服务雪崩就形成了.

![image](https://segmentfault.com/img/bVziad)

#### 雪崩形成的原因

* 服务提供者不可用
  
* 重试加大流量
  
* 服务调用者不可用

服务雪崩的每个阶段都可能由不同的原因造成, 比如造成 服务不可用 的原因有:

硬件故障

程序Bug

缓存击穿

用户大量请求

硬件故障可能为硬件损坏造成的服务器主机宕机, 网络硬件故障造成的服务提供者的不可访问.
缓存击穿一般发生在缓存应用重启, 所有缓存被清空时,以及短时间内大量缓存失效时. 大量的缓存不命中, 使请求直击后端,造成服务提供者超负荷运行,引起服务不可用.
在秒杀和大促开始前,如果准备不充分,用户发起大量请求也会造成服务提供者的不可用.

而形成 重试加大流量 的原因有:

用户重试

代码逻辑重试

在服务提供者不可用后, 用户由于忍受不了界面上长时间的等待,而不断刷新页面甚至提交表单.
服务调用端的会存在大量服务异常后的重试逻辑.
这些重试都会进一步加大请求流量.

最后, 服务调用者不可用 产生的主要原因是:

同步等待造成的资源耗尽

当服务调用者使用 同步调用 时, 会产生大量的等待线程占用系统资源. 一旦线程资源被耗尽,服务调用者提供的服务也将处于不可用状态, 于是服务雪崩效应产生了.

#### 服务雪崩的应对策略

针对造成服务雪崩的不同原因, 可以使用不同的应对策略:

* 流量控制

* 改进缓存模式

* 服务自动扩容

* 服务调用者降级服务

**流量控制** 的具体措施包括:

    网关限流

    用户交互限流

    关闭重试
    
因为Nginx的高性能, 目前一线互联网公司大量采用Nginx+Lua的网关进行流量控制;
 
用户交互限流的具体措施有: 

    1. 采用加载动画,提高用户的忍耐等待时间. 
    2. 提交按钮添加强制等待时间机制.
 
**改进缓存模式:**
    
    缓存预加载
    
    同步改为异步刷新
    
**服务调用者降级服务:**

    资源隔离

    不可用服务的调用快速失败 
    
资源隔离主要是对调用服务的线程池进行隔离.
 
不可用服务的调用快速失败一般通过 超时机制, 熔断器 和熔断后的 降级方法 来实现.

#### 使用Hystrix预防雪崩

1.断路器机制

断路器很好理解, 当Hystrix Command请求后端服务失败数量超过一定比例(默认50%), 断路器会切换到开路状态(Open). 这时所有请求会直接失败而不会发送到后端服务. 
断路器保持在开路状态一段时间后(默认5秒), 自动切换到半开路状态(HALF-OPEN). 这时会判断下一次请求的返回情况, 如果请求成功, 断路器切回闭路状态(CLOSED), 
否则重新切换到开路状态(OPEN). Hystrix的断路器就像我们家庭电路中的保险丝, 一旦后端服务不可用, 断路器会直接切断请求链,避免发送大量无效请求影响系统吞吐量, 
并且断路器有自我检测并恢复的能力.

2.Fallback

Fallback相当于是降级操作. 对于查询操作, 我们可以实现一个fallback方法, 当请求后端服务出现异常的时候, 可以使用fallback方法返回的值. 
fallback方法的返回值一般是设置的默认值或者来自缓存.

3.资源隔离

在Hystrix中, 主要通过线程池来实现资源隔离. 通常在使用的时候我们会根据调用的远程服务划分出多个线程池. 例如调用产品服务的Command放入A线程池,
调用账户服务的Command放入B线程池. 这样做的主要优点是运行环境被隔离开了. 这样就算调用服务的代码存在bug或者由于其他原因导致自己所在线程池被耗尽时, 
不会对系统的其他服务造成影响. 但是带来的代价就是维护多个线程池会对系统带来额外的性能开销. 如果是对性能有严格要求而且确信自己调用服务的客户端代码不会出问题的话, 
可以使用Hystrix的信号模式(Semaphores)来隔离资源.

例如:

在一个高度服务化的系统中,我们实现的一个业务逻辑通常会依赖多个服务,比如:
商品详情展示服务会依赖商品服务, 价格服务, 商品评论服务. 如图所示:

![segmentfault.com/a/1190000005988895](https://segmentfault.com/img/bVzh9U)

调用三个依赖服务会共享商品详情服务的线程池. 如果其中的商品评论服务不可用, 就会出现线程池里所有线程都因等待响应而被阻塞, 从而造成服务雪崩. 如图所示:

![](https://segmentfault.com/img/bVzh9S)

Hystrix通过将每个依赖服务分配独立的线程池进行资源隔离, 从而避免服务雪崩.
如下图所示, 当商品评论服务不可用时, 即使商品服务独立分配的20个线程全部处于同步等待状态,也不会影响其他依赖服务的调用.

![](https://segmentfault.com/img/bVziah)

**创建 hystrix-consumer-service**

引入依赖:

```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>0.9.0.RELEASE</version>
        </dependency>
    </dependencies>
```
使用nacos作为服务注册发现;其中openfeign中已经包含了hystrix的依赖;

配置文件:

```properties
server.port=8765
spring.application.name=service-hystrix
feign.hystrix.enabled=true
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

创建CaseServiceClient:

```java
@FeignClient(value = "service-say",fallbackFactory = CaseServiceFallback.class )
public interface CaseServiceClient {

    @RequestMapping(value = "/say",method = RequestMethod.GET)
    String sayFromClient(@RequestParam(value = "name") String name);
}
```
有很多案例中都是使用fallback,这里我使用的是fallbackFactory,这两个的区别是fallbackFactory可以返回失败的原因;例如下面的`throwable`

创建fallback:

```java
@Component
public class CaseServiceFallback implements FallbackFactory<CaseServiceClient> {
    @Override
    public CaseServiceClient create(Throwable throwable) {
        return new CaseServiceClient() {
            @Override
            public String sayFromClient(String name) {
                return "sorry " + name +" error cause: "+ throwable;
            }
        };
    }
}
```
编写一个Controller

```java
@RestController
public class CaseController{

    @Autowired
    CaseServiceClient caseServiceClient;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return caseServiceClient.sayFromClient( name );
    }
}
```
在启动类上加上

`@EnableFeignClients`和 `@EnableDiscoveryClient`注解;

我们启动项目并浏览器访问 http://localhost:8765/hi?name=haoxy ,注意这时我们并没有启动`hystrix-provider-service`,因为还没有创建这个工程;

页面显示:

`sorry haoxy error cause: java.lang.RuntimeException: com.netflix.client.ClientException: Load balancer does not have available server for client: service-say`

**创建 hystrix-provider-service工程**

添加依赖:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
   <version>0.9.0.RELEASE</version>
</dependency>
```

增加配置

```properties
server.port=8075
spring.application.name=service-say
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

创建Controller

```java
@RestController
public class ProviderController {

    @Value("${server.port}")
    String port;

    @RequestMapping("/say")
    public String home(@RequestParam(value = "name", defaultValue = "haoxy") String name) {
        return "hi " + name + " ,i am from port:" + port;
    }
}
```
启动 hystrix-provider-service工程,并同样在浏览器访问: http://localhost:8765/hi?name=haoxy

页面显示:

`hi haoxy ,i am from port:8075`


 
 

