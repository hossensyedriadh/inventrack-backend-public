package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum InvitedUserAuthority {
    ROLE_ADMINISTRATOR ("Admin"),
    ROLE_MODERATOR ("Moderator");

    private final String value;

    InvitedUserAuthority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
