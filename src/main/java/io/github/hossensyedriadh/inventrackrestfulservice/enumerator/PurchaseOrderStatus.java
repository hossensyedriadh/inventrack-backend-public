package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum PurchaseOrderStatus {
    PENDING ("Pending"),
    IN_STOCK ("In Stock"),
    CANCELLED ("Cancelled");

    private final String value;

    PurchaseOrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
