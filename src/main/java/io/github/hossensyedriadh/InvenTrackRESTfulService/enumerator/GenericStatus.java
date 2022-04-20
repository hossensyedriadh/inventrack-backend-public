package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum GenericStatus {
    SUCCESSFUL ("Successful"),
    FAILED ("Failed");

    private final String value;

    GenericStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
