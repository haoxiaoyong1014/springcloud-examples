package cn.haoxiaoyong.hystrix.consumer.controller;

import cn.haoxiaoyong.hystrix.consumer.client.CaseServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoxiaoyong on 2020/3/16 下午 3:57
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@RestController
public class CaseController{

    @Autowired
    CaseServiceClient caseServiceClient;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return caseServiceClient.sayFromClient( name );
    }

}
