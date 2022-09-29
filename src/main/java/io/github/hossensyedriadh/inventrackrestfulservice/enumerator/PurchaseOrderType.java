package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum PurchaseOrderType {
    NEW_PRODUCT ("New Product"),
    RESTOCK ("Restock");

    private final String value;

    PurchaseOrderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
