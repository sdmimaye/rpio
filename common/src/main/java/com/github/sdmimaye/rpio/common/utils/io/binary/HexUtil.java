package com.github.sdmimaye.rpio.common.utils.io.binary;

public class HexUtil {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    private HexUtil() {
    }

    public static String toHex(byte[] bytes) {
        if(bytes.length <= 0)
            return "";

        return toHex(bytes, SpaceMode.WITH_SPACES);
    }

    public static String toHex(byte singleByte) {
        return toHex(new byte[]{singleByte}, SpaceMode.WITH_SPACES);
    }

    public static String toHex(byte[] bytes, SpaceMode spaceMode) {
        if (bytes == null) {
            return null;
        }

        char[] charsWithoutSpace = printHexBinary(bytes).toCharArray();

        if (spaceMode == SpaceMode.WITH_SPACES) {
            char[] charsWithSpaces = new char[charsWithoutSpace.length + charsWithoutSpace.length / 2 - 1];
            for (int i = 0; i < charsWithoutSpace.length / 2; i++) {
                charsWithSpaces[i * 3] = charsWithoutSpace[i * 2];
                charsWithSpaces[i * 3 + 1] = charsWithoutSpace[i * 2 + 1];
                int spacePosition = i * 3 + 2;
                if (spacePosition < charsWithSpaces.length) {
                    charsWithSpaces[spacePosition] = ' ';
                }
            }

            return new String(charsWithSpaces);
        } else if (spaceMode == SpaceMode.NO_SPACES) {
            return new String(charsWithoutSpace);
        } else {
            throw new IllegalArgumentException("space mode must be given");
        }
    }

    public static byte[] toBytes(String string) {
        if (string == null) {
            return null;
        }

        string = string.replaceAll("0x", "");
        string = string.replaceAll(",", "");
        string = string.replaceAll(" ", "");

        return parseHexBinary(string);
    }

    public static String reverse(String originalString) {
        if (originalString.contains(" "))
            throw new UnsupportedOperationException("Not supported for strings with space characters");

        StringBuilder resultBuilder = new StringBuilder(originalString.length());
        char[] originalChars = originalString.toCharArray();
        for (int i = originalChars.length; i > 1; i -= 2) {
            resultBuilder.append(originalChars[i - 2]);
            resultBuilder.append(originalChars[i - 1]);
        }

        return resultBuilder.toString();
    }

    private static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    private static byte[] parseHexBinary(String s) {
        final int len = s.length();

        // "111" is not a valid hex encoding.
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        }

        byte[] out = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            int h = hexToBin(s.charAt(i));
            int l = hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
            }

            out[i / 2] = (byte) (h * 16 + l);
        }

        return out;
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        return -1;
    }

    public enum SpaceMode {
        WITH_SPACES, NO_SPACES
    }
}
