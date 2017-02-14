package com.github.sdmimaye.rpio.server.database.models.enums;

public enum DbType {
    Local("jdbc:hsqldb:file:", ";crypt_key=" + getC(System.currentTimeMillis()) + ";crypt_type=AES;"),
    MsSql("jdbc:jtds:sqlserver://", "/"),
    Postgresql("jdbc:postgresql://", "/"),
    Oracle("jdbc:oracle:thin:@", "");

    private final String prefix;
    private final String postfix;

    DbType(String prefix, String postfix) {
        this.prefix = prefix;
        this.postfix = postfix;
    }

    private static String getC(long s) {
        String builder = "C0486FB8978E9ECC35D0B9D429D033C8";
        long a = 0;
        for (int i = 0; i < 4 + (s % 100); i++) {
            if (i == 0) {
                builder = builder.substring(4) + "ABCD";
            }

            a += (s << i);
            a = Math.abs(a) % 100;
            i += a;
        }

        return builder;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPostfix() {
        return postfix;
    }

    public String build(String host, String database) {
        switch (this) {
            case MsSql:
                return buildMsSql(host, database);
            default:
                return this.prefix + host + postfix + database;
        }
    }

    private String[] splitHostAndInstance(String host) {
        String splitCharacter = host.contains("/") ? "/" : "\\\\";
        return host.split(splitCharacter);
    }

    private String buildMsSql(String host, String database) {
        String[] split = splitHostAndInstance(host);
        if(split.length == 1)//no instance name
            return this.prefix + host + postfix + database;

        return this.prefix + split[0] + postfix + database + ";instance=" + split[1];
    }
}
