package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum Authority {
    ROLE_ROOT ("Root"),
    ROLE_ADMINISTRATOR ("Admin"),
    ROLE_MODERATOR ("Moderator");

    private final String simpleValue;

    Authority(String simpleValue) {
        this.simpleValue = simpleValue;
    }

    public String getSimpleValue() {
        return simpleValue;
    }
}
