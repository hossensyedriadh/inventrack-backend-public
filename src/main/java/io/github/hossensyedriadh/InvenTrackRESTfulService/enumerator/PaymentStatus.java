package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum PaymentStatus {
    PENDING("Pending"),
    COMPLETED("Completed"),
    PARTIAL ("Partial"),
    CANCELLED("Cancelled");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
