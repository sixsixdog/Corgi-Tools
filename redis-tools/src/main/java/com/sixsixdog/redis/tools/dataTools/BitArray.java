package com.sixsixdog.redis.tools.dataTools;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @Package: com.sixsixdog.redis.tools.dataTools
 * @ClassName: BitArray
 * @Author: Sixsixdog
 * @CreateTime: 2023-05-15 15:52
 * @Description:
 */
public class BitArray {
    private final static BigDecimal two = new BigDecimal(2);
    private long bitValue;

    public long getBitValue() {
        return bitValue;
    }

    BitArray(long bitValue) {
        this.bitValue = bitValue;
    }

    BitArray(int... bitArray) {
        bitValue = 0;
        for (int pos : bitArray) {
            bitValue |= pos;
        }
    }

    private long num2bit(int num) {
        return two.pow(num - 1).longValue();
    }

    @Override
    public String toString() {
        return Long.toBinaryString(bitValue);
    }

    public boolean getIndex(int num) {
        if (num > 64) {
            throw new RuntimeException("超过64位");
        }
        return (bitValue & num2bit(num)) > 0;
    }

    public void setIndexBit(int num, int i) {
        long l = num2bit(num);
        if (i == 0) {
            bitValue &= ~l;
        } else {
            bitValue |= l;
        }
    }
}
