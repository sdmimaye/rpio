package com.github.sdmimaye.rpio.common.io.binary;

import com.github.sdmimaye.rpio.common.utils.io.binary.HexUtil;

public class Bits {
    private final byte mask;

    public static Bits byIndicies(int... indicies) {
        byte value = 0;
        for (int index : indicies) {
            value = setBit(value, index);
        }

        return new Bits(value);
    }

    public static Bits byMask(byte mask) {
        return new Bits(mask);
    }

    private Bits(byte mask) {
        this.mask = mask;
    }

    public byte getMask() {
        return mask;
    }

    public static byte changeBit(boolean expression, byte mask, int index) {
        if(expression) {
            return setBit(mask, index);
        }else if(isBit(mask, index)){
            return removeBit(mask, index);
        }

        return mask;
    }

    public static boolean isBit(byte mask, int index) {
        byte bitmask = (byte) (Math.pow(2, index));
        return (mask & bitmask) == bitmask;
    }

    public static boolean isBit(short mask, int index) {
        short bitmask = (short) (Math.pow(2, index));
        return (mask & bitmask) == bitmask;
    }

    public static boolean isBit(int mask, int index) {
        int bitmask = (int) (Math.pow(2, index));
        return (mask & bitmask) == bitmask;
    }

    public static byte setBit(byte mask, int index) {
        return (byte)(mask | (byte)Math.pow(2, index));
    }

    public static byte removeBit(byte mask, int index) {
        return (byte)(mask ^ (byte)Math.pow(2, index));
    }

    public static byte setBits(byte mask, byte bits) {
        return (byte) (mask | bits);
    }

    public static byte removeBits(byte mask, byte bits) {
        return (byte) (mask ^ bits);
    }

    public static boolean isBits(byte mask, byte bits) {
        return (mask & bits) == bits;
    }

    @Override
    public String toString() {
        return "Bits{" +
                HexUtil.toHex(mask) +
                "}";
    }
}
