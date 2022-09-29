package io.github.hossensyedriadh.inventrackrestfulservice.enumerator;

public enum FinanceRecordType {
    EXPENSE ("Expense"),
    SALE ("Sale");

    private final String value;

    FinanceRecordType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
