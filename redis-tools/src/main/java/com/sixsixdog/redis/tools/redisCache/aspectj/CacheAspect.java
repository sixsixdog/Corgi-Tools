package com.sixsixdog.redis.tools.redisCache.aspectj;

import com.sixsixdog.redis.tools.log.ColorLog;
import com.sixsixdog.redis.tools.redisCache.handler.DefaultGenerateKey;
import com.sixsixdog.redis.tools.redisCache.annotation.CacheHelper;
import com.sixsixdog.redis.tools.redistool.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

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
@ConditionalOnExpression("${corgi.redis.cache.enable:false}")
public class CacheAspect {
    static ColorLog log = new ColorLog();
    CacheAspect() {
        log.info( "缓存切片CacheAspect已创建");
    }
//    static Logger log = LoggerFactory.getLogger(CacheAspect.class);
    static Lock reenLock = new ReentrantLock(true);
    static Condition condition = reenLock.newCondition();
    @Autowired
    private RedisUtil redisUtil;
    private static boolean outputAll = true;


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
            log.error("缓存切面错误,无法获取注解,未启用缓存获取:实例[{}]-对象[{}]-方法[{}]", aClass.getName(), signature, signature.getMethod().getName());
            return proceed;
        }
        //检查必要参数
        if (StringUtils.isBlank(annotation.key()) && annotation.generateKey() == null) {
            Object proceed = point.proceed();
            log.error("缓存切面错误,未指定缓存key和key生成器,未启用缓存获取");
            return proceed;
        }
        String cacheKey = null;
        //若没有key传入使用key工厂生成key
        if (StringUtils.isBlank(annotation.key())) {
            Class generateKey = annotation.generateKey();
            DefaultGenerateKey generate = (DefaultGenerateKey) generateKey.newInstance();
            cacheKey = generate.generateKey(signature, point);
        } else {
            cacheKey = annotation.key();
        }
        //根据缓存是否命中更新或获取缓存
        return getSetCache(annotation.log(), cacheKey, annotation, point);
    }

    private Object getSetCache(boolean bOutput, String key, CacheHelper anno, ProceedingJoinPoint point) throws Throwable {
        Object proceed;
        Long expireTime;
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
                        log.info("数据库获取:[{}]", key);
                        //获取数据库数据
                        proceed = point.proceed();
                    } else {
                        log.info("二次确认缓存命中:[{}]", key);
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
            int random = getExpireTime(anno);
            //是否缓存空对象
            if (anno.cacheNull()) {
                log.info("缓存空对象:key[{}] , 缓存时效{}", key,random);
                redisUtil.set(key, proceed, random);
            } else {
                if (!Objects.isNull(proceed)) {
                    log.info("缓存对象:key[{}] , 缓存时效{}", key,random);
                    redisUtil.set(key, proceed, random);
                }else
                    log.info("未缓存空对象:key[{}]", key);
            }
            return proceed;
        } else {
            expireTime = redisUtil.getExpire(key);
            log.info("缓存命中:[{}],剩余时效:{}", key,expireTime);
            return cache;
        }
    }
    private static int  getExpireTime(CacheHelper annotation) {
        int expireTime = 0;
        if (annotation.randomExpire()) {
            expireTime = (int) (expireTime + annotation.minValue() + Math.random() * (annotation.maxValue() - annotation.minValue()) + 1);
        }else{
            expireTime = annotation.expire();
        }
        return expireTime;
    }

}