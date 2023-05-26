package com.sixsixdog.redis.tools.redisCache.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Package: com.sixsixdog.redis.tools.redisCache.handler
 * @ClassName: ParamFilterHandler
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-25 10:38
 * @Description:
 */
public abstract class ParamFilterHandler {

    ParamFilterHandler(){
        //去除java虚拟地址
        addFilter((s)-> s.replaceAll("@[a-zA-Z0-9]*", ""));
    }
    protected List<Function<String, String>> filters = new ArrayList<>();
    /**
     * 增加过滤器
     */
    final void addFilter(Function<String, String> filter){
        filters.add(filter);
    }
}
