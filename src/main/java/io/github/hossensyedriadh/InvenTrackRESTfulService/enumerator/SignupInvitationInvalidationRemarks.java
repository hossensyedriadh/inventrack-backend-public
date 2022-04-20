package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum SignupInvitationInvalidationRemarks {
    USED ("Used"),
    EXPIRED ("Expired"),
    REVOKED ("Revoked");

    private final String value;

    SignupInvitationInvalidationRemarks(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
