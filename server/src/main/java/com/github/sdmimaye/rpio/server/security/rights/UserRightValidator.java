package com.github.sdmimaye.rpio.server.security.rights;

import com.github.sdmimaye.rpio.server.database.models.enums.Right;
import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.security.rights.exceptions.RightMalformedException;
import com.github.sdmimaye.rpio.server.security.rights.exceptions.UnknownRightEntityException;
import com.github.sdmimaye.rpio.server.security.rights.exceptions.UnknownRightException;

import java.util.Arrays;
import java.util.function.Function;

public class UserRightValidator {
    public static final String IS_LOGGED_IN = "is-logged-in";
    public static final String IS_ADMIN = "is-admin";

    public boolean hasAllRights(User user, String[] rights){
        return Arrays.asList(rights)
                .stream()
                .allMatch(right -> hasAccess(user, right));
    }

    public boolean hasAccess(User user, String rights) {//rights -> "person-read"
        if(user == null)
            return false;

        if(user.getRoles().stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsAdmin())))//administrators can do anything
            return true;

        if(Boolean.TRUE.equals(user.getSuperAdmin()))
            return true;

        if(rights.equals(IS_LOGGED_IN))
            return true;

        if(rights.equals(IS_ADMIN))
            return false;

        String[] all = rights.split(",");
        for (String one : all) {
            String[] seperated = one.split("-");
            if(seperated.length != 2)
                throw new RightMalformedException(rights);

            Function<Role, Right> selector;
            switch (seperated[0]){//entity
                case "test":
                    selector = (r) -> null;
                    break;
                default:
                    throw new UnknownRightEntityException(seperated[0]);
            }

            final Right right = parseRight(seperated[1]);
            boolean access = user.getRoles().stream().anyMatch(r -> selector.apply(r).isAllowed(right));
            if(!access)
                return false;
        }

        return true;
    }

    private Right parseRight(String strRight){
        Right right = Right.NONE;
        try {
            right = Enum.valueOf(Right.class, strRight.toUpperCase());
        }catch (Exception ex){
            throw new UnknownRightException(strRight);
        }

        return right;
    }
}
