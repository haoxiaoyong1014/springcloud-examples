package cn.haoxiaoyong.openfeign.consumer.client.fallback;

import cn.haoxiaoyong.openfeign.consumer.client.StoreFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author haoxiaoyong on 2020/3/21 下午 10:09
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@Component
@Slf4j
public class StoreFallbackFactory implements FallbackFactory<StoreFeignClient> {
    @Override
    public StoreFeignClient create(Throwable throwable) {
        return new StoreFeignClient() {
            @Override
            public String getStoreByName(String name) {
                log.info("sorry " + name + " Exception " + throwable);
                return "sorry " + name + " Exception " + throwable;
            }
        };
    }
}
