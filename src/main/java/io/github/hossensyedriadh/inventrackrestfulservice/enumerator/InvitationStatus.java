package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum InvitationStatus {
    VALID ("Valid"),
    INVALID ("Invalid");

    private final String value;

    InvitationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
