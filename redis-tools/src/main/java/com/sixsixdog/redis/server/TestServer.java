package com.sixsixdog.redis.server;

import org.springframework.stereotype.Service;

/**
 * @Package: com.sixsixdog.redis.server
 * @ClassName: TestServer
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-24 16:07
 * @Description:
 */
@Service
public class TestServer {
    public String test(){
        return "ok";
    }
}
