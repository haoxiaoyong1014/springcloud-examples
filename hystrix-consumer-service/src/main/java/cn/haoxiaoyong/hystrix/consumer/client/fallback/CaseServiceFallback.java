package cn.haoxiaoyong.hystrix.consumer.client.fallback;

import cn.haoxiaoyong.hystrix.consumer.client.CaseServiceClient;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author haoxiaoyong on 2020/3/16 下午 3:31
 * e-mail: hxyHelloWorld@163.com
 * github: https://github.com/haoxiaoyong1014
 * Blog: www.haoxiaoyong.cn
 */
@Component
public class CaseServiceFallback implements FallbackFactory<CaseServiceClient> {
    @Override
    public CaseServiceClient create(Throwable throwable) {
        return new CaseServiceClient() {
            @Override
            public String sayFromClient(String name) {
                return "sorry " + name +" error cause: "+ throwable;
            }
        };
    }
}
