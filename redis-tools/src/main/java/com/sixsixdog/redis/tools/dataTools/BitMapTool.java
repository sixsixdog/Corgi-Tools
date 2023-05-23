package com.sixsixdog.redis.tools.dataTools;

import org.springframework.stereotype.Component;

/**
 * @Package: com.sixsixdog.redis.tools.dataTools
 * @ClassName: BitMapTool
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-15 15:37
 * @Description:
 */
@Component
public class BitMapTool {
    Boolean compareBit(BitArray src,BitArray dest) {
        long template = Math.max(src.getBitValue(), dest.getBitValue());
        long pattern = Math.min(src.getBitValue(), dest.getBitValue());
        return (template | pattern) > pattern;
    }
}

