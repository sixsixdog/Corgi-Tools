package com.sixsixdog.redis.tools.redistool;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public RedisUtil() {
    }

    /**
     * 指定缓存失效时间
     * @param key
     * @param time
     * @return
     */
    public boolean expire(String key, long time){
        try{
            if (time > 0L){
                this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }

    }

    /**
     * 根据key获取过期时间
     * @param key
     * @return
     */
    public long getExpire(String key){
        return this.redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断缓存中是否含有key
     * @param key
     * @return
     */
    public boolean hasKey(String key){
        try{
            return this.redisTemplate.hasKey(key);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 批量删除缓存
     * @param key
     */
    public void del(String...key){
        if (key != null && key.length > 0){
            if (key.length == 1){
                this.redisTemplate.delete(key[0]);
            }else {
                this.redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 根据key获取缓存
     * @param key
     * @return
     */
    public Object get(String key){
        return key == null ? null : this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通存入缓存数据
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value){
        try{
            this.redisTemplate.opsForValue().set(key, value);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 普通存入缓存，并设置时间
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, Object value, long time){
        try{
            if (time > 0L)
                this.redisTemplate.opsForValue().set(key,value, time, TimeUnit.SECONDS);
            else
                this.set(key,value);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 批量存入缓存，并设置时间
     * @param list
     * @param time
     * @return
     */
    public boolean setlist(List<HashMap<String,String>> list, long time){
        try{
            if (time > 0L) {
                for(HashMap<String,String> v : list){
                    this.redisTemplate.opsForValue().set(v.get("CBH"), v.get("CMC"),time,TimeUnit.SECONDS);
                }
            }
            else{
                for(HashMap<String,String> v : list){

                    for(Map.Entry<String, String> kv : v.entrySet()){
                        this.set(kv.getKey(),kv.getValue(),time);
                    }
                }

            }

            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 递增
     * @param key
     * @param delta
     * @return
     */
    public long incr(String key, long delta){
        if (delta < 0L){
            throw new RuntimeException("递增因子必须大于0");
        }else
            return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key
     * @param delta
     * @return
     */
    public long decr(String key, long delta){
        if (delta < 0L){
            throw new RuntimeException("递增因子必须大于0");
        }else
            return this.redisTemplate.opsForValue().increment(key, -delta);
    }

    /**
     * 获取所有
     * @return
     */
    public Set<String> getAll(){
        return this.redisTemplate.keys("*");
    }

    /**
     * hashget
     * @param key
     * @param item
     * @return
     */
    public Object hget(String key, String item){
        return this.redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashkey对应的所有键值
     * @param key
     * @return
     */
    public Map<Object, Object> hmget(String key){
        return this.redisTemplate.opsForHash().entries(key);
    }

    /**
     * hashset
     * @param key
     * @param map
     * @return
     */
    public boolean hmset(String key, Map<String, Object> map){
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * hashset 并设置时间
     * @param key
     * @param map
     * @param time
     * @return
     */
    public boolean hmset(String key, Map<String, Object> map, long time){
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            if (time > 0L){
                this.expire(key, time);
            }
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 向一张表中放入数据，如果不存在就创建
     * @param key
     * @param item
     * @param value
     * @return
     */
    public boolean hset(String key, String item, Object value){
        try{
            this.redisTemplate.opsForHash().put(key, item, value);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key
     * @param item
     * @param value
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return
     */
    public boolean hset(String key, String item, Object value, long time){
        try{
            this.redisTemplate.opsForHash().put(key, item, value);
            if (time > 0L)
                this.expire(key, time);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key
     * @param item
     */
    public void hdel(String key, Object...item){
        this.redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表里是否有改key值
     * @param key
     * @param item
     * @return
     */
    public boolean hHasKey(String key, String item){
        return this.redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增，如果不存在就会创建一个，并把新增的值返回
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hincr(String key, String item, double by){
        return this.redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减，如果不存在就会创建一个，并把减少的值返回
     * @param key
     * @param item
     * @param by
     * @return
     */
    public double hdecr(String key, String item, double by){
        return this.redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 根据key获取set中的所有值
     * @param key
     * @return
     */
    public Set<Object> sGet(String key){
        try {
            return this.redisTemplate.opsForSet().members(key);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 根据value从一个set中查询是否存在
     * @param key
     * @param value
     * @return
     */
    public boolean sHasKey(String key, Object value){
        try{
            return this.redisTemplate.opsForSet().isMember(key, value);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key
     * @param values
     * @return
     */
    public long sSet(String key, Object... values){
        try {
            return this.redisTemplate.opsForSet().add(key,values);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return 0L;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key
     * @param values
     * @return
     */
    public long sSetAndTime(String key, long time, Object... values){
        try{
            Long count = this.redisTemplate.opsForSet().add(key, values);
            if (time > 0L){
                this.expire(key, time);
            }
            return count;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return 0L;
        }
    }

    /**
     * 获取set缓存数据的长度
     * @param key
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return this.redisTemplate.opsForSet().size(key);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return 0L;
        }
    }

    /**
     * 获取list缓存的内容
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> lGet(String key, long start, long end){
        try{
            return this.redisTemplate.opsForList().range(key, start, end);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key
     * @return
     */
    public long lGetListSize(String key){
        try {
            return this.redisTemplate.opsForList().size(key);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return 0L;
        }
    }

    /**
     * 通过索引 获取list缓存中的值
     * @param key
     * @param index
     * @return
     */
    public Object lGetIndex(String key, long index){
        try {
            return this.redisTemplate.opsForList().index(key, index);
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return 0L;
        }
    }

    /**
     * 将list放入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean lSet(String key, Object value){
        try {
            this.redisTemplate.opsForList().rightPush(key, value);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
    public boolean lSet(String key, Object value, long time){
        try {
            this.redisTemplate.opsForList().rightPush(key, value);
            if (time > 0L){
                this.expire(key, time);
            }
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
    public boolean lSet(String key,List<Object> value){
        try {
            this.redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
    public boolean lSet(String key, List<Object> value, long time){
        try {
            this.redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0L){
                this.expire(key, time);
            }
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 根据索引 修改list缓存中的某个数据
     * @param key
     * @param index
     * @param value
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value){
        try {
            this.redisTemplate.opsForList().set(key, index, value);
            return true;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 移除n个值为value
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long lRemove(String key, long count, Object value){
        try {
            Long remove = this.redisTemplate.opsForList().remove(key, count, value);
            return remove;
        }catch (Exception e){
            this.log.error(ExceptionUtils.getStackTrace(e));
            return 0L;
        }
    }


}
