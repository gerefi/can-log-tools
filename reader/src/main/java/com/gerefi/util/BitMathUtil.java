package com.gerefi.util;

public class BitMathUtil {
    public static int mask(int bitWidth) {
        return (1 << bitWidth) - 1;
    }
}
