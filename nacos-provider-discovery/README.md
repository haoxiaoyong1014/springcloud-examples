### nacos-provider-discovery 使用 Nacos做服务注册和发现

#### 服务注册

在本案例中，使用2个服务注册到Nacos上，分别为nacos-provider-discovery和nacos-consumer-discovery。

**构建服务提供者nacos-provider-discovery**
新建一个Spring Boot项目，Spring boot版本为2.1.4.RELEASE，Spring Cloud 版本为Greenwich.RELEASE，在pom文件引入nacos的Spring Cloud起步依赖，代码如下：

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>0.9.0.RELEASE</version>
        </dependency>
        
在工程的配置文件application.yml做相关的配置，配置如下：

```properties
server.port=8762
spring.application.name=nacos-provider
spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
```

在上述的配置的中，程序的启动端口为8762，应用名为nacos-provider，向nacos server注册的地址为127.0.0.1:8848。

然后在Spring Boot的启动文件NacosProviderApplication加上@EnableDiscoveryClient注解，代码如下:

```java
@SpringBootApplication
@EnableDiscoveryClient
public class NacosProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(NacosProviderApplication.class, args);
	}

}
```

**构建服务消费者nacos-consumer-discovery**

和nacos-provider-discovery一样,构建服务消费者nacos-consumer-discovery,nacos-consumer-discovery的启动端口8763。构建过程同nacos-provider-discovery，这里省略。

**验证服务注册和发现**

分别启动2个工程，待工程启动成功之后，在访问localhost:8848，可以发现nacos-provider和nacos-consumer，均已经向nacos-server注册，如下图所示：

![image.png](https://upload-images.jianshu.io/upload_images/15181329-05e09400f47dbe5a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这时我们更改nacos-provider-discovery项目的端口号为8764,启动两个nacos-provider-discovery;

![image.png](https://upload-images.jianshu.io/upload_images/15181329-f12264282ce2f0e6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

点击详情:

![image.png](https://upload-images.jianshu.io/upload_images/15181329-d155805224974982.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 服务调用

nacos作为服务注册和发现组件时，在进行服务消费，可以选择RestTemplate和Feign等方式。这和使用Eureka和Consul作为服务注册和发现的组件是一样的，没有什么区别。这是因为spring-cloud-starter-alibaba-nacos-discovery依赖实现了Spring Cloud服务注册和发现的相关接口，可以和其他服务注册发现组件无缝切换。

#### 提供服务

在nacos-provider-discovery工程，写一个Controller提供API服务，代码如下:

```java
@RestController
public class ProviderController {

    private static Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @GetMapping("/hi")
    public String hi(@RequestParam(value = "name", defaultValue = "haoxy", required = false) String name) {

        logger.info(name);

        return "hi " + name;
    }
}
```

#### 消费服务

使用FeignClient调用服务

在nacos-consumer-discovery的pom文件引入以下的依赖:

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<dependencyManagement>
    <dependencies>
       <dependency>
          <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-dependencies</artifactId>
           <version>Greenwich.RELEASE</version>
           <type>pom</type>
           <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

```
在NacosConsumerApplication启动文件上加上@EnableFeignClients注解开启FeignClient的功能。

```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NacosConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }
}
```

在nacos-consumer-discovery项目中写一个FeignClient，调用nacos-provider-discovery的服务，代码如下:

```java
@FeignClient("nacos-provider")
public interface ProviderClient {

   @GetMapping("/hi")
   String hi(@RequestParam(value = "name", defaultValue = "haoxy", required = false) String name);
}
```
写一个消费API，该API使用ProviderClient来调用nacos-provider-discovery的API服务，代码如下:

```java
@RestController
public class ConsumerController {

    @Autowired
    ProviderClient providerClient;

    @GetMapping("/hi-feign")
    public String hiFeign(){
        return providerClient.hi("feign");
    }
}

```
重启工程，在浏览器上访问http://localhost:8763/hi-feign，可以在浏览器上展示正确的响应，这时nacos-consumer-discovery调用nacos-provider-discovery服务成功。

可以多访问几次,看看落到8764,8762各个的概率情况;

#### Nacos优雅的下线

![image.png](https://upload-images.jianshu.io/upload_images/15181329-d155805224974982.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这时服务提供者nacos-provider-discovery是有2个实例一个是8764,8762;我们访问http://localhost:8763/hi-feign时,各自都有50%的概率;
这时我们可以将8764端口上的下线;

#### Nacos设置权重

Nacos支持权重配置，这是个比较实用的功能，例如：

* 把性能差的机器权重设低，性能好的机器权重设高，让请求优先打到性能高的机器上去；
* 某个实例出现异常时，把权重设低，排查问题，问题排查完再把权重恢复；
* 想要下线某个实例时，可先将该实例的权重设为0，这样流量就不会打到该实例上了——此时再去关停该实例，这样就能实现优雅下线啦。
但是很遗憾,目前权重编辑是不生效的; <a href="http://www.itmuch.com/spring-cloud-alibaba/ribbon-nacos-weight">扩展Ribbon支持Nacos权重的三种方式</a>
