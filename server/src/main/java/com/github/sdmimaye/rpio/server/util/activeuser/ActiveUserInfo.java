package com.github.sdmimaye.rpio.server.util.activeuser;

import com.github.sdmimaye.rpio.server.database.models.system.User;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ActiveUserInfo {
    private final Long userId;
    private final String userName;

    public ActiveUserInfo(Long userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public static ActiveUserInfo forUser(User user) {
        return new ActiveUserInfo(user.getId(), user.getLoginName());
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("userId", userId).
                append("userName", userName).
                toString();
    }
}
