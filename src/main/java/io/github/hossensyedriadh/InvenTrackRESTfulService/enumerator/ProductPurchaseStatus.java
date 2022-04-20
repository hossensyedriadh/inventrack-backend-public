package io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator;

public enum ProductPurchaseStatus {
    PENDING ("Pending"),
    IN_STOCK ("In Stock"),
    CANCELLED ("Cancelled");

    private final String value;

    ProductPurchaseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
