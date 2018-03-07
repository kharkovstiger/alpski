package model.utils;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_GM,
    ROLE_MOD,
    ROLE_MANAGER;

    @Override
    public String getAuthority() {
        return name();
    }
}
