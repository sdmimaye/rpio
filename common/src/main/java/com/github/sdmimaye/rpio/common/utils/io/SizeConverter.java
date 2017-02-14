package com.github.sdmimaye.rpio.common.utils.io;

import java.text.DecimalFormat;

public class SizeConverter {
    private SizeConverter() {
    }

    public static String convertByteToString(long byteSize) {
        DecimalFormat twoPrecision = new DecimalFormat("#.##");

        if (byteSize < 1024) {
            return byteSize + " Byte";
        } else if (byteSize < Math.pow(1024, 2)) {
            return twoPrecision.format(byteSize / 1024) + " KiB";
        } else if (byteSize < Math.pow(1024, 3)) {
            return twoPrecision.format(byteSize / Math.pow(1024, 2)) + " MiB";
        } else if (byteSize < Math.pow(1024, 4)) {
            return twoPrecision.format(byteSize / Math.pow(1024, 3)) + " GiB";
        } else if (byteSize < Math.pow(1024, 5)) {
            return twoPrecision.format(byteSize / Math.pow(1024, 4)) + " TiB";
        } else {
            return twoPrecision.format(byteSize / Math.pow(1024, 5)) + " PiB";
        }
    }
}
