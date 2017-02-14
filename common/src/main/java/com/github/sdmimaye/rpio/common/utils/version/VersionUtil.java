package com.github.sdmimaye.rpio.common.utils.version;

import com.google.inject.Singleton;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class that loads version information from any available source while
 * executing the static initializer.
 */
@Singleton
public class VersionUtil {
    private static final Object initialisationLock = new Object();
    private static final String REGEX_REVISION_FROM_SVNVERSION_OUTPUT = "^(\\d+)";
    private static final String PATH_TO_VERSION_TXT = "../../version.txt";
    private static final String REGEX_HOTFIX_FROM_VERSION_NUMBER = "^\\d+\\.\\d+\\.(\\d+)\\.\\d+$";
    private static final String REGEX_BRANCH_FROM_VERSION_NUMBER = "^\\d+\\.(\\d+)\\.\\d+\\.\\d+$";
    private static final String REGEX_MAJOR_FROM_VERSION_NUMBER = "^(\\d+)\\.\\d+\\.\\d+\\.\\d+$";
    private static final String REGEX_REVISION_FROM_VERSION_NUMBER = "^\\d+\\.\\d+\\.\\d+\\.(\\d+)$";
    private static volatile String fullVersionString;
    private static volatile int major;
    private static volatile int branch;
    private static volatile int hotfix;
    private static volatile int revision;
    private static volatile boolean initialised = false;

    private static void initIfNecessary(Class<?> componentClass) {
        if (initialised) return;

        synchronized (initialisationLock) {
            if (initialised) return;

            try {
                fullVersionString = getFullVersionString(componentClass);
                major = extractMajorFromVersionString(fullVersionString);
                branch = extractBranchFromVersionString(fullVersionString);
                hotfix = extractHotfixFromVersionString(fullVersionString);
                revision = extractRevisionFromVersionString(fullVersionString);
            } catch (Exception e) {
                System.err.println("Error while loading version information: " + e.getMessage());
                revision = 0;
            }

            initialised = true;
        }
    }

    private static String getFullVersionString(Class<?> componentClass) throws IOException, InterruptedException {
        String versionFromManifest = getVersionFromManifest(componentClass);
        if (versionFromManifest != null) return versionFromManifest;

        String versionFromTextFile = getVersionFromTextFile();
        if (versionFromTextFile != null) return versionFromTextFile;

        throw new IOException("Could not retrieve revision from svnversion or parse it from manifest.");
    }

    private static int extractHotfixFromVersionString(String versionString) {
        return extractIntUsingRegex(versionString, REGEX_HOTFIX_FROM_VERSION_NUMBER, "hotfix number");
    }

    private static int extractBranchFromVersionString(String versionString) {
        return extractIntUsingRegex(versionString, REGEX_BRANCH_FROM_VERSION_NUMBER, "branch number");
    }

    private static int extractMajorFromVersionString(String versionString) {
        return extractIntUsingRegex(versionString, REGEX_MAJOR_FROM_VERSION_NUMBER, "major version number");
    }

    private static int extractRevisionFromVersionString(String versionString) {
        return extractIntUsingRegex(versionString, REGEX_REVISION_FROM_VERSION_NUMBER, "revision");
    }

    private static int extractIntUsingRegex(String target, String regex, String targetType) {
        String stringValue = extractUsingRegex(target, regex, targetType);
        try {
            return Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            System.err
                    .println("Could not convert parsed " + targetType + " value '" + stringValue + "' to an integer!");
            return 0;
        }
    }

    private static String extractUsingRegex(String target, String regex, String targetName) {
        Matcher matcher = Pattern.compile(regex).matcher(target);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            System.err
                    .println("Could not parse " + targetName + " from string output '" + target + "' using regex '" + regex + "'.");
            return "0";
        }
    }

    private static File getVersionTextFile(){
        return getVersionFileRecursive(Paths.get("").toAbsolutePath().toFile());
    }

    private static File getVersionFileRecursive(File file) {
        if(file.isFile() && file.getName().equalsIgnoreCase("version.txt"))
            return file;

        if(!file.isDirectory()) return null;

        File[] files = file.listFiles();
        if(files == null || files.length == 0)
            return null;

        for (File current : files) {
            if(current.isFile() && current.getName().equalsIgnoreCase("version.txt"))
                return current;
        }

        return getVersionFileRecursive(file.getParentFile());
    }

    private static String getVersionFromTextFile() throws IOException, InterruptedException {
        return FileUtils.readFileToString(getVersionTextFile());
    }

    private static String extractRevisionStringFromSvnversionOutput(String svnversionOutput) {
        return extractUsingRegex(svnversionOutput, REGEX_REVISION_FROM_SVNVERSION_OUTPUT, "revision");
    }

    private static String getVersionFromManifest(Class<?> componentClass) {
        Package componentPackage = componentClass.getPackage();
        return componentPackage.getSpecificationVersion();
    }

    public static int getRevisionNumber(Class<?> componentClass) {
        initIfNecessary(componentClass);
        return revision;
    }

    public static int getMajorNumber(Class<?> componentClass) {
        initIfNecessary(componentClass);
        return major;
    }

    public static int getBranchNumber(Class<?> componentClass) {
        initIfNecessary(componentClass);
        return branch;
    }

    public static int getHotfixNumber(Class<?> componentClass) {
        initIfNecessary(componentClass);
        return hotfix;
    }

    public static String getVersionString(Class<?> componentClass) {
        initIfNecessary(componentClass);
        return fullVersionString;
    }
}
