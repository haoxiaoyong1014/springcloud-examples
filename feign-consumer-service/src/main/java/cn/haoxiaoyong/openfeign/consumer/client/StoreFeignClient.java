package cn.haoxiaoyong.openfeign.consumer.client;

import cn.haoxiaoyong.openfeign.consumer.client.fallback.StoreFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author haoxiaoyong on 2020/3/21 下午 9:41
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */


@FeignClient(value = "service-say", fallbackFactory = StoreFallbackFactory.class)
public interface StoreFeignClient {


    /**
     * get
     * @param name
     * @return
     */
    @RequestMapping(value = "/say",method = RequestMethod.GET)
    String getStoreByName(@RequestParam(value = "name") String name);
}
