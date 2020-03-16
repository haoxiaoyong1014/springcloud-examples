package cn.haoxiaoyong.hystrix.consumer.client;

import cn.haoxiaoyong.hystrix.consumer.client.fallback.CaseServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author haoxiaoyong on 2020/3/16 下午 3:24
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@FeignClient(value = "service-say",fallbackFactory = CaseServiceFallback.class )
public interface CaseServiceClient {

    @RequestMapping(value = "/say",method = RequestMethod.GET)
    String sayFromClient(@RequestParam(value = "name") String name);
}
