### nacos-config-project 使用Nacos做服务配置中心


#### 下载Nacos

**从 Github 上下载源码方式**
```json
git clone https://github.com/alibaba/nacos.git
cd nacos/
mvn -Prelease-nacos -Dmaven.test.skip=true clean install -U  
ls -al distribution/target/

// change the $version to your actual path
cd distribution/target/nacos-server-$version/nacos/bin
```
**下载编译后压缩包方式**

您可以从 <a href="https://github.com/alibaba/nacos/releases">最新稳定版本</a> 下载 nacos-server-$version.zip 包。
```json
  unzip nacos-server-$version.zip 或者 tar -xvf nacos-server-$version.tar.gz
  cd nacos/bin
```
#### 启动服务器

**Linux/Unix/Mac**
启动命令(standalone代表着单机模式运行，非集群模式):

`sh startup.sh -m standalone`

如果您使用的是ubuntu系统，或者运行脚本报错提示[[符号找不到，可尝试如下运行：

`bash startup.sh -m standalone`

**Windows**
启动命令：

`cmd startup.cmd`

或者双击startup.cmd运行文件。

在浏览器中输入:

http://127.0.0.1:8848/nacos

用户名:nacos 密码: nacos

会出现大致下面的界面:

![image.png](https://upload-images.jianshu.io/upload_images/15181329-0498f222725a7751.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

下面正式开始使用Nacos做服务配置中心

#### 创建SpringBoot项目

Spring boot版本为2.1.4.RELEASE,在pom文件引入nacos的Spring Cloud起步依赖,代码如下代码如下:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-alibaba-nacos-config</artifactId>
    <version>0.9.0.RELEASE</version>
</dependency>
```

在bootstrap.properties(一定是bootstrap.properties/yml文件，不是application.yml文件)文件配置以下内容:

```properties
spring.application.name=nacos-config
spring.cloud.nacos.config.server-addr=127.0.0.1:8848
spring.cloud.nacos.config.file-extension=properties
spring.cloud.nacos.config.prefix=nacos-config
spring.profiles.active=dev
```

在上面的配置中，配置了nacos config server的地址，配置的扩展名是properties（目前仅支持yaml和properties）。注意是没有配置server.port的，sever.port的属性在nacos中配置。上面的配置是和Nacos中的dataId 的格式是对应的，nacos的完整格式如下:

`${prefix}-${spring.profile.active}.${file-extension}`

* prefix 默认为 spring.application.name 的值，也可以通过配置项 spring.cloud.nacos.config.prefix来配置。
* spring.profile.active 即为当前环境对应的 profile，详情可以参考 <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html#boot-features-profiles">Spring Boot文档</a>。 注意：当 spring.profile.active 为空时，对应的连接符 - 也将不存在，dataId 的拼接格式变成 ${prefix}.${file-extension}
* file-exetension 为配置内容的数据格式，可以通过配置项 spring.cloud.nacos.config.file-extension 来配置。目前只支持 properties 和 yaml 类型。

启动nacos,登陆localhost:8848/nacos，**创建**一个data id ，完整的配置如图所示：

![image.png](https://upload-images.jianshu.io/upload_images/15181329-28f20478a5676223.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

写一个RestController,在Controller上添加 @RefreshScope 实现配置的热加载。代码如下：

```java
@RestController
@RefreshScope
public class ConfigController {

    @Value("${username:lily}")
    private String username;

    @RequestMapping("/username")
    public String get() {
        return username;
    }
}
```

启动工程，在浏览器上访问localhost:8760/username，可以返回在nacos控制台上配置的username。在nacos 网页上更改username的配置，在不重启工程的情况下，重新访问localhost:8760/username，返回的是修改后的值，可见nacos作为配置中心实现了热加载功能。
