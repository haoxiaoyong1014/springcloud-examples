### feign-consumer-service

#### 使用okhttp替换Feign默认Client

Feign 在默认情况下使用的是 JDK 原生的 URLConnection 发送HTTP请求，没有连接池，但是对每个地址会保持一个长连接，即利用 HTTP 的 persistence connection。我们可以用 OKHttp 去替换 Feign 默认的 Client。
```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-okhttp</artifactId>
</dependency>
```
增加配置:

```yaml
feign:
  okhttp:
    enabled: true
```
增加配置类:

```java
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class FeignClientOkHttpConfiguration {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 连接超时
                .connectTimeout(20, TimeUnit.SECONDS)
                // 响应超时
                .readTimeout(20, TimeUnit.SECONDS)
                // 写超时
                .writeTimeout(20, TimeUnit.SECONDS)
                // 是否自动重连
                .retryOnConnectionFailure(true)
                // 连接池
                .connectionPool(new ConnectionPool())
                .build();
    }
}

```

####  三者之间的关系图

在Spring Cloud微服务体系下，微服务之间的互相调用可以通过Feign进行声明式调用，在这个服务调用过程中Feign会通过Ribbon从服务注册中心获取目标微服务的服务器地址列表，之后在网络请求的过程中Ribbon就会将请求以负载均衡的方式打到微服务的不同实例上，从而实现Spring Cloud微服务架构中最为关键的功能即服务发现及客户端负载均衡调用。

另一方面微服务在互相调用的过程中，为了防止某个微服务的故障消耗掉整个系统所有微服务的连接资源，所以在实施微服务调用的过程中我们会要求在调用方实施针对被调用微服务的熔断逻辑。而要实现这个逻辑场景在Spring Cloud微服务框架下我们是通过Hystrix这个框架来实现的。

调用方会针对被调用微服务设置调用超时时间，一旦超时就会进入熔断逻辑，而这个故障指标信息也会返回给Hystrix组件，Hystrix组件会根据熔断情况判断被调微服务的故障情况从而打开熔断器，之后所有针对该微服务的请求就会直接进入熔断逻辑，直到被调微服务故障恢复，Hystrix断路器关闭为止。

三者之间的关系图，大致如下：

![](https://cg-mall.oss-cn-shanghai.aliyuncs.com/cg/images/bolg_3.png)


#### 超时问题

Feign中集成了Hystrix和Ribbon,当我们配置了`feign.client.config.default.connectTimeout=5000`和`feign.client.config.default.readTimeout=5000`时,我们在提供方
休眠4s,这时请求 http://localhost:9876/store?name=haoxy 这时发现走了降级策略,并返回错误信息
`com.netflix.hystrix.exception.HystrixTimeoutException`;出现这个错误也是意料之中的事情，因为Hystrix的默认超时时间是1s!,所以会直接走到降级策略！
当我们配置了`hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=10000`时,继续访问,发现这次正常打印结果！

Ribbon的超时时间配置与Hystrix的超时时间配置则存在依赖关系，因为涉及到Ribbon的重试机制，所以一般情况下都是Ribbon的超时时间小于Hystrix的超时时间,

**Ribbon和Hystrix的超时时间配置的关系**

那么Ribbon和Hystrix的超时时间配置的关系具体是什么呢？如下：

`Hystrix的超时时间=Ribbon的重试次数(包含首次) * (ribbon.ReadTimeout + ribbon.ConnectTimeout)`

而Ribbon的重试次数的计算方式为：

`Ribbon重试次数(包含首次)= 1 + ribbon.MaxAutoRetries  +  ribbon.MaxAutoRetriesNextServer  +  (ribbon.MaxAutoRetries * ribbon.MaxAutoRetriesNextServer)`

以上图中的Ribbon配置为例子，Ribbon的重试次数=1+(1+1+1)=4，所以Hystrix的超时配置应该>=4*(3000+3000)=24000毫秒。在Ribbon超时但Hystrix没有超时的情况下，Ribbon便会采取重试机制；而重试期间如果时间超过了Hystrix的超时配置则会立即被熔断（fallback）。

如果不配置Ribbon的重试次数，则Ribbon默认会重试一次，加上第一次调用Ribbon，总的的重试次数为2次，以上述配置参数为例，Hystrix超时时间配置为2*6000=12000，由于很多情况下，大家一般不会主动配置Ribbon的重试次数，所以这里需要注意下！强调下，以上超时配置的值只是示范，超时配置有点大不太合适实际的线上场景，大家根据实际情况设置即可！

说明下，如果不启用Hystrix，Feign的超时时间则是Ribbon的超时时间，Feign自身的配置也会被覆盖。


