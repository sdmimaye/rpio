package com.github.sdmimaye.rpio.common.io;

import com.github.sdmimaye.rpio.common.utils.state.OsUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryUtils {
    public static File getSystemTempFolder(){
        if (OsUtil.isWindows()) {
            return new File(System.getenv("WINDIR") + "\\TEMP\\");
        }else{
            return new File("/tmp");
        }
    }

    public static List<File> findFilesByExtention(File directory, String extention) {
        File[] files = directory.listFiles((dir, name) -> name.endsWith(extention));
        if(files == null || files.length <= 0)
            return new ArrayList<>();

        return Arrays.stream(files).collect(Collectors.toList());
    }
}
