package com.sixsixdog.redis.tools.redisCache.handler;

import com.sixsixdog.redis.tools.log.ColorLog;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @Package: com.sixsixdog.redis.tools.redisCache.handler
 * @ClassName: DefualtParamFilterHandler
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-25 10:55
 * @Description:
 */
@Component
@ConditionalOnMissingBean(ParamFilterHandler.class)
public class DefualtParamFilterHandler extends ParamFilterHandler{
    ColorLog log = new ColorLog();
    public DefualtParamFilterHandler(){
        super();
        log.info("创建DefualtParamFilterHandler");
        //去除时间戳(此处是个性需求,可自行修改)
        addFilter((s)->{
            return s.replaceAll(",\\s*_t=\\d*", "");
        });
    }
}
