package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum SignupInvitationStatus {
    VALID ("Valid"),
    INVALID ("Invalid");

    private final String value;

    SignupInvitationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
