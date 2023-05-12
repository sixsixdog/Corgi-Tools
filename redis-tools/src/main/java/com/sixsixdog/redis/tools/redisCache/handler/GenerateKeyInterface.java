package com.sixsixdog.redis.tools.redisCache.handler;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @Package: io.zhkj.common.handler
 * @ClassName: GenerateKeyInterface
 * @Author: Sixsixdog
 * @CreateTime: 2023-03-29 16:51
 * @Description:
 */
public interface GenerateKeyInterface {
    String preKeys = null;
    String generateKey(MethodSignature signName, JoinPoint jp);
}
