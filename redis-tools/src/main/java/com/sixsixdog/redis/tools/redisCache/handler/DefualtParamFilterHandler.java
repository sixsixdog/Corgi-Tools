package com.sixsixdog.redis.tools.redisCache.handler;

import com.sixsixdog.redis.tools.log.ColorLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Conditional;

/**
 * @Package: com.sixsixdog.redis.tools.redisCache.handler
 * @ClassName: DefualtParamFilterHandler
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-25 10:55
 * @Description:
 */
@ConditionalOnExpression("${corgi.redis.cache.enable:false}")
public class DefualtParamFilterHandler extends ParamFilterHandler{
    ColorLog log = new ColorLog();
    public DefualtParamFilterHandler(){
        super();
        log.info("缓存默认条件参数过滤器DefualtParamFilterHandler已创建");
        //去除时间戳(此处是个性需求,可自行修改)
        addFilter((s)-> s.replaceAll(",\\s*_t=\\d*", ""));
    }
}
