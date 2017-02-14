package com.github.sdmimaye.rpio.common.utils.io.binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ByteUtil {
    public static byte[] concatBytes(Object... byteElements) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (Object byteElement : byteElements) {
            try {
                if (byteElement instanceof Byte) {
                    stream.write((Byte) byteElement);
                } else if (byteElement instanceof byte[]) {
                    stream.write((byte[]) byteElement);
                } else if (byteElement instanceof Integer) {
                    stream.write((Integer) byteElement);
                } else if (byteElement instanceof String) {
                    stream.write(toBytes((String) byteElement));
                } else {
                    throw new IllegalArgumentException("illegal argument type for '" + byteElement + "'");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return stream.toByteArray();
    }

    public static byte[] rotateBytesLeft(byte[] original) {
        int originalLength = original.length;
        byte[] rotated = new byte[originalLength];
        for (int i = 0; i < originalLength; i++) {
            rotated[i] = original[(i + 1) % originalLength];
        }

        return rotated;
    }

    public static byte[] rotateBytesRight(byte[] original) {
        int originalLength = original.length;
        byte[] rotated = new byte[originalLength];
        for (int i = 0; i < originalLength; i++) {
            rotated[i] = original[(originalLength + i - 1) % originalLength];
        }

        return rotated;
    }

    public static byte[] toBytes(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int stringLength = hexString.length();
        byte[] data = new byte[stringLength / 2];

        for (int i = 0; i < stringLength; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }

        return data;
    }

    public static byte[] toPadded(byte[] data, int padding) {
        int remaining = data.length % padding;
        if(remaining == 0) return data;//is multiple of padding

        int missing = padding - remaining;
        if(missing == 0 || missing == padding) return data;//is multiple of padding

        byte[] buffer = new byte[data.length + missing];
        System.arraycopy(data, 0, buffer, 0, data.length);

        return buffer;
    }

    public static byte[] toNonZeroBytes(byte[] data) {
        int lastNonZeroByteIndex = data.length - 1;
        while (lastNonZeroByteIndex >= 0){
            if(data[lastNonZeroByteIndex--] != 0)
                break;
        }

        if(lastNonZeroByteIndex <= 0)
            return new byte[0];

        byte[] copy = new byte[lastNonZeroByteIndex];
        System.arraycopy(data, 0, copy, 0, lastNonZeroByteIndex);

        return copy;
    }
}
