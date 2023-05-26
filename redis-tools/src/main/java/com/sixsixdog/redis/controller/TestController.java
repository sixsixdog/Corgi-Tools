package com.sixsixdog.redis.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Package: com.sixsixdog.redis.controller
 * @ClassName: testController
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-24 14:57
 * @Description:
 */

@RestController
public class TestController {
    @GetMapping("test")
    public String test(){
            return "ok";
        }
}
