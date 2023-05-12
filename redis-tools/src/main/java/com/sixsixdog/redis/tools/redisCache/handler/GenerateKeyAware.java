package com.sixsixdog.redis.tools.redisCache.handler;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @Package: io.zhkj.common.handler
 * @ClassName: GenerateKeyInterface
 * @Author: Sixsixdog
 * @CreateTime: 2023-03-25 15:05
 * @Description:
 */

public class GenerateKeyAware implements GenerateKeyInterface{
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
            String data = arg.toString().replaceAll("@[a-zA-Z0-9]*", "");
            //去除时间戳
            data = data.replaceAll(",\\s*_t=\\d*", "");
            sb.append(data);
            sb.append(",");
        }
        return preKeys + sb.toString();
    }

}
