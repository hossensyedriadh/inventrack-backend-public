package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum OrderStatus {
    PENDING ("Pending"),
    CONFIRMED ("Confirmed"),
    CANCELLED ("Cancelled");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
