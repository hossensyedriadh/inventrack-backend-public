package io.github.hossensyedriadh.inventrackrestfulservice.service.count;

public interface CountService {
    Double totalSales();

    Double totalCost();

    Integer unitsSold();

    Double stockAvailable();
}
