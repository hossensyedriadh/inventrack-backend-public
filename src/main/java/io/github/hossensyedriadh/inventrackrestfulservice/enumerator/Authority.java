package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum Authority {
    ROLE_ROOT ("Root"),
    ROLE_ADMINISTRATOR ("Admin"),
    ROLE_MODERATOR ("Moderator");

    private final String value;

    Authority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
