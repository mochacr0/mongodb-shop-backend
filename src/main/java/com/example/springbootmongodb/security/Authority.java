package com.example.springbootmongodb.security;

public enum Authority {
    USER,
    ADMIN,
    REFRESH_TOKEN;

    public static Authority parseFromString(String value) {
        Authority authority = null;
        if (value != null && value.length() > 0) {
            for (Authority currentAuthority : values()) {
                if (currentAuthority.name().equalsIgnoreCase(value)) {
                    authority = currentAuthority;
                    break;
                }
            }
        }
        return authority;
    }
}
