package com.sixsixdog.redis.tools.redisCache.annotation;

import com.sixsixdog.redis.tools.redisCache.handler.GenerateKeyAware;
import com.sixsixdog.redis.tools.redisCache.handler.GenerateKeyInterface;

import java.lang.annotation.*;

/**
 * @Package: io.zhkj.common.annotation
 * @ClassName: Cacheable
 * @Author: Sixsixdog
 * @CreateTime: 2023-03-25 14:56
 * @Description: 基于方法的缓存支持注解,依赖于redisTemplate,使用时请确保redis已经被容器化
 * 若出现autoType错误,请检查pom文件中的fastjson版本 或添加自动映射白名单
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheHelper {
    /**
     * 缓存key
     * @return
     */
    String key() default "";

    /**
     * 缓存key生成接口,用于自定义key生成规则
     * @return 返回以类名函数为分组:参数为key的值
     */
    Class<? extends GenerateKeyInterface> generateKey() default GenerateKeyAware.class;
    /**
     * 缓存时间(秒)
     * @return
     */
    int expire() default 600;

    /**
     * 是否缓存空值(缓存穿透)
     * @return
     */
    boolean cacheNull() default false;

    /**
     * 过期时间差异化(增加随机时间处理缓存雪崩)
     * @return
     */
    boolean randomExpire() default true;

    /**
     * 随机时间上界
     * @return
     */
    int maxValue() default 600;

    /**
     * 随机时间下界
     * @return
     */
    int minValue() default 300;
    /**
     * 是否输出日志
     */
    boolean log() default false;
}
