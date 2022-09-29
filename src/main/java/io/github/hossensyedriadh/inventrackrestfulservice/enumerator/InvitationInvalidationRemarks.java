package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum InvitationInvalidationRemarks {
    USED ("Used"),
    EXPIRED ("Expired"),
    REVOKED ("Revoked");

    private final String value;

    InvitationInvalidationRemarks(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
