package cn.haoxiaoyong.discovery.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author haoxiaoyong on 2020/3/10 下午 9:54
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@FeignClient("nacos-provider")
public interface ProviderClient {

    /**
     * @param name
     * @return
     */
    @GetMapping("/hi")
    String hi(@RequestParam(value = "name", defaultValue = "haoxy", required = false) String name);
}
