package com.github.sdmimaye.rpio.common.utils.io.binary;

import java.util.zip.CRC32;

public class CrcUtil {
    public static byte[] getCrc32(byte[] data) {
        return getCrc32(data, 0, data.length);
    }
    public static byte[] getCrc32(byte[] data, int offset, int length) {
        CRC32 crc32 = new CRC32();
        crc32.update(data, offset, length);
        long value = crc32.getValue();
        return new byte[]{
                (byte) (value & 0xff),
                (byte) ((value >>> 8) & 0xff),
                (byte) ((value >>> 16) & 0xff),
                (byte) ((value >>> 24) & 0xff)};
    }
}
