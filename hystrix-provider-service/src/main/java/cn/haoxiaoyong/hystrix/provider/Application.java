package cn.haoxiaoyong.hystrix.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author haoxiaoyong on 2020/3/16 下午 3:46
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@SpringBootApplication
@EnableDiscoveryClient

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
