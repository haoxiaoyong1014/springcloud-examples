package cn.haoxiaoyong.openfeign.consumer.controller;

import cn.haoxiaoyong.openfeign.consumer.client.StoreFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoxiaoyong on 2020/3/21 下午 10:23
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@RestController
public class StoreController {

    @Autowired
    StoreFeignClient storeFeignClient;

    @RequestMapping("store")
    public String getStoreByName(@RequestParam(value = "name") String name) {

        return storeFeignClient.getStoreByName(name);
    }
}
