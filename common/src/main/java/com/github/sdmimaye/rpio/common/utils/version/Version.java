package com.github.sdmimaye.rpio.common.utils.version;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version>, Serializable {
    private static final Pattern PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

    private int major;
    private int minor;
    private int build;
    private int revision;

    public Version() {

    }

    public Version(int major, int minor, int build, int revision) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.revision = revision;
    }

    public static Version fromByteArray(byte[] array, int offset) {
        return new Version(array[offset], array[offset + 1], array[offset + 2], array[offset + 3]);
    }

    public static Version tryParse(byte[] data) {
        if(data.length != 4 && data.length != 16)
            return null;

        ByteBuffer buffer = ByteBuffer.wrap(data);
        if(data.length == 4) {
            return new Version(buffer.get(), buffer.get(), buffer.get(), buffer.get());
        }else {
            return new Version(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt());
        }
    }

    public static Version tryParse(String text) {
        if(StringUtils.isBlank(text))
            return null;

        Matcher matcher = PATTERN.matcher(text);
        if(!matcher.find())
            return null;

        String group = matcher.group(0);
        String[] splitt = group.split("\\.");
        if (splitt.length != 4)
            return null;

        try {
            int major = Integer.parseInt(splitt[0]);
            int minor = Integer.parseInt(splitt[1]);
            int build = Integer.parseInt(splitt[2]);
            int revision = Integer.parseInt(splitt[3]);

            return new Version(major, minor, build, revision);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isBetween(Version current, Version min, Version max){
        Objects.requireNonNull(current, "Current must not be null");
        Objects.requireNonNull(min, "Min must not be null");
        Objects.requireNonNull(max, "Max must not be null");

        int minimum = current.compareTo(min);
        if(minimum == -1)
            return false;

        int maximum = current.compareTo(max);
        return maximum == -1;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getName(){
        return major + "." + minor + "." + build + "." + revision;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Version && compareTo((Version) obj) == 0;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(Version other) {
        if(other == null)
            return 1;

        if(major > other.major) return 1;
        else if(major < other.major) return -1;

        if(minor > other.minor) return 1;
        else if(minor < other.minor) return -1;

        if(build > other.build) return 1;
        else if(build < other.build) return -1;

        if(revision > other.revision) return 1;
        else if(revision < other.revision) return -1;

        return 0;
    }

    public byte[] toCustomerId(){
        if(major < 0 || major > 255 || minor < 0 || minor > 255 || build < 0 || build > 255 || revision < 0 || revision > 255)
            throw new RuntimeException("Invalid Customer ID. Only Values between 0 and 255 are valid");

        return new byte[]{
            (byte)major, (byte)minor, (byte)build, (byte)revision
        };
    }

    public boolean isHigherOrEqualsThan(int major, int minor, int build, int revision) {
        Version version = new Version(major, minor, build, revision);
        return this.compareTo(version) != -1;
    }
}
