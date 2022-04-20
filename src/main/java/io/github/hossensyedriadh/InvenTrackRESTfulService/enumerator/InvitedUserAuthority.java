package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum InvitedUserAuthority {
    ROLE_ADMINISTRATOR("Admin"),
    ROLE_MODERATOR("Moderator");

    private final String simpleValue;

    InvitedUserAuthority(String simpleValue) {
        this.simpleValue = simpleValue;
    }

    public String getSimpleValue() {
        return simpleValue;
    }
}
