package com.github.thedeathlycow.tnsparkour.helpers;

import java.math.BigInteger;

public class PrimitiveSizes {

    public static int sizeof(byte b) {
        return 8;
    }

    public static int sizeof(short s) {
        return 16;
    }

    public static int sizeof(int i) {
        return 32;
    }

    public static int sizeof(long l) {
        return 64;
    }

    public static int sizeof(BigInteger big) {
        return big.toByteArray().length * 8;
    }

    public static int sizeof(char c) {
        return 16;
    }
}
