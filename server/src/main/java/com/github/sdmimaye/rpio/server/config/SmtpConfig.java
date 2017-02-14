package com.github.sdmimaye.rpio.server.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sdmimaye.rpio.common.config.core.Config;
import com.github.sdmimaye.rpio.server.database.models.enums.SmtpSecurityType;
import org.apache.commons.lang3.StringUtils;

public class SmtpConfig implements Config {
    private String email;
    private String name;
    private String login;
    private String password;
    private SmtpSecurityType security;
    private String host;
    private int port;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SmtpSecurityType getSecurity() {
        return security;
    }

    public void setSecurity(SmtpSecurityType security) {
        this.security = security;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @JsonIgnore
    public boolean isValidConfiguration(){
        String[] ss = new String[]{email, name, host, login, password};
        for (String s : ss) if(StringUtils.isEmpty(s)) return false;

        int[] is = new int[]{port};
        for (int i : is) if(i == 0) return false;

        Object[] os = new Object[]{security};
        for(Object o : os) if(o == null) return false;

        return true;
    }

    @Override
    public void reset() {
        email = name = host = null;
        port = 0;
        security = null;
    }

    public SmtpConfig noPassword() {
        password = null;
        return this;
    }
}
