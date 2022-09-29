package io.github.hossensyedriadh.inventrackrestfulservice.service.report;

import java.util.Map;

public interface ReportService {
    Map<Integer, Integer> unitsSold();

    Map<Integer, Integer> unitsSold(int year);

    Integer unitsSold(int year, int month);

    Map<Integer, Integer> purchaseOrderCount();

    Map<Integer, Integer> purchaseOrderCount(int year);

    Integer purchaseOrderCount(int year, int month);

    Map<Integer, Integer> saleOrderCount();

    Map<Integer, Integer> saleOrderCount(int year);

    Integer saleOrderCount(int year, int month);
}
