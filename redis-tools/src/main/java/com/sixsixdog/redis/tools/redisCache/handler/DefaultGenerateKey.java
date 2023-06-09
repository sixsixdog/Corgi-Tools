package com.sixsixdog.redis.tools.redisCache.handler;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Function;

/**
 * @Package: io.zhkj.common.handler
 * @ClassName: GenerateKeyInterface
 * @Author: Sixsixdog
 * @CreateTime: 2023-03-25 15:05
 * @Description:
 */

public class DefaultGenerateKey implements GenerateKeyInterface{
    @Autowired(required = false)
    private ParamFilterHandler filterHandlerMap;

    String preKeys = "cache:";
    @Override
    public String generateKey(MethodSignature signName, JoinPoint jp) {
        StringBuilder sb = new StringBuilder();
        sb.append(signName.getDeclaringType());
        sb.append(":");
        sb.append(signName.getName());
        sb.append(":");
        Object[] args = jp.getArgs();
        for (Object arg : args) {
            String data = arg.toString();
            if(Objects.nonNull(filterHandlerMap)){
                for(Function<String, String> filter : filterHandlerMap.filters){
                    data = filter.apply(data);
                }
            }
            sb.append(data);
            sb.append(",");
        }


        return preKeys + sb;
    }

}
