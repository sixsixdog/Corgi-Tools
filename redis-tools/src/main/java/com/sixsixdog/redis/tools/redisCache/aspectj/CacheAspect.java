package com.sixsixdog.redis.tools.redisCache.aspectj;

import com.sixsixdog.redis.tools.redisCache.handler.GenerateKeyAware;
import com.sixsixdog.redis.tools.redisCache.annotation.CacheHelper;
import com.sixsixdog.redis.tools.redistool.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Package: io.zhkj.common.aspect
 * @ClassName: CacheAspect
 * @Author: Sixsixdog
 * @CreateTime: 2023-03-25 14:57
 * @Description: 基于方法的缓存支持
 */
@Order
@Aspect
@Component
public class CacheAspect {
    static Logger log = LoggerFactory.getLogger(CacheAspect.class);
    static Lock reenLock = new ReentrantLock(true);
    static Condition condition = reenLock.newCondition();
    @Autowired
    private RedisUtil redisUtil;
    private static boolean outputAll = true;

    //随机时间生成,静态内联优化
    private static int random(int val, int max, int min) {
        return (int) (val + min + Math.random() * (max - min) + 1);
    }

    private static void info(boolean bOutput, String msg) {
        if (bOutput || outputAll) {
            log.debug(msg);
        }
    }

    private static void info(boolean bOutput, String msg, Object... params) {
        if (bOutput || outputAll) {
            log.debug(msg, params);
        }
    }

    private static void debug(boolean bOutput, String msg) {
        if (bOutput || outputAll) {
            log.debug(msg);
        }
    }

    private static void debug(boolean bOutput, String msg, Object... params) {
        if (bOutput || outputAll) {
            log.debug(msg, params);
        }
    }

    private static void error(String msg) {
        log.error(msg);
    }

    private static void error(String msg, Object... params) {
        log.error(msg, params);
    }


    @Pointcut("@annotation(com.sixsixdog.redis.tools.redisCache.annotation.CacheHelper)")
    public void cachePointCut() {
    }

    @Around("cachePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Class<?> aClass = point.getTarget().getClass();
        MethodSignature signature = (MethodSignature) point.getSignature();
        //获取目标对象
        CacheHelper annotation = AnnotationUtils.findAnnotation(signature.getMethod(), CacheHelper.class);
        if (annotation == null) {
            Object proceed = point.proceed();
            error("缓存切面错误,无法获取注解,未启用缓存获取:实例[{}]-对象[{}]-方法[{}]", aClass.getName(), signature, signature.getMethod().getName());
            return proceed;
        }
        //检查必要参数
        if (StringUtils.isBlank(annotation.key()) && annotation.generateKey() == null) {
            Object proceed = point.proceed();
            error("缓存切面错误,未指定缓存key和key生成器,未启用缓存获取");
            return proceed;
        }
        String cacheKey = null;
        //若没有key传入使用key工厂生成key
        if (StringUtils.isBlank(annotation.key())) {
            Class generateKey = annotation.generateKey();
            GenerateKeyAware generate = (GenerateKeyAware) generateKey.newInstance();
            cacheKey = generate.generateKey(signature, point);
        } else {
            cacheKey = annotation.key();
        }
        //根据缓存是否命中更新或获取缓存
        return getSetCache(annotation.log(), cacheKey, annotation, point);
    }

    private Object getSetCache(boolean bOutput, String key, CacheHelper anno, ProceedingJoinPoint point) throws Throwable {
        Object proceed;
        //尝试获取缓存
        Object cache = redisUtil.get(key);
        //缓存未命中
        if (Objects.isNull(cache)) {
            //单个线程获取锁,其他线程进入自旋状态
            if (reenLock.tryLock()) {
                try {
                    //双重检测防止重入 (缓存击穿)
                    proceed = redisUtil.get(key);
                    //确认缓存未命中后进行缓存更新
                    if (Objects.isNull(proceed)) {
                        info(bOutput, "数据库获取:{}", key);
                        //获取数据库数据
                        proceed = point.proceed();
                    } else {
                        info(bOutput, "二次确认缓存命中:{}", key);
                    }
                } finally {
                    //condition.signalAll();
                    reenLock.unlock();
                }
            } else {
                //休眠
                //condition.await();
                Thread.yield();
                return getSetCache(bOutput, key, anno, point);
            }
            //获取随机过期时间
            int random = 0;
            //随机过期时间计算
            if (anno.randomExpire()) {
                random = random(anno.expire(), anno.maxValue(), anno.minValue());
            } else {
                random = anno.expire();
            }
            //是否缓存空对象
            if (anno.cacheNull()) {
                redisUtil.set(key, proceed, random);
            } else {
                if (!Objects.isNull(proceed)) {
                    redisUtil.set(key, proceed, random);
                }
            }
            return proceed;
        } else {
            info(bOutput,"缓存命中:{}", key);
            return cache;
        }
    }

}