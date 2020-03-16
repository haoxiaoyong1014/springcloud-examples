package cn.haoxiaoyong.hystrix.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoxiaoyong on 2020/3/16 下午 3:47
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@RestController
public class ProviderController {

    @Value("${server.port}")
    String port;

    @RequestMapping("/say")
    public String home(@RequestParam(value = "name", defaultValue = "haoxy") String name) {
        return "hi " + name + " ,i am from port:" + port;
    }
}
