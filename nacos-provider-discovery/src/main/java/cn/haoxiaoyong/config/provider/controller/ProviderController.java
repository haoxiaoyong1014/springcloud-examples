package cn.haoxiaoyong.config.provider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author haoxiaoyong on 2020/3/10 下午 6:40
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@RestController
public class ProviderController {

    private static Logger logger = LoggerFactory.getLogger(ProviderController.class);

    @GetMapping("/hi")
    public String hi(@RequestParam(value = "name", defaultValue = "haoxy", required = false) String name) {

        logger.info(name);

        return "hi " + name;
    }
}
