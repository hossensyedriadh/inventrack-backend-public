package io.github.hossensyedriadh.InvenTrackRESTfulService.service.report;

import java.util.Map;

public sealed interface ReportService permits ReportServiceImpl {
    Map<Integer, Integer> getUnitsSold();

    Map<Integer, Integer> getUnitsSold(int year);

    Map<Integer, Integer> getPurchaseOrderCount();

    Map<Integer, Integer> getPurchaseOrderCount(int year);

    Map<Integer, Integer> getSaleOrderCount();

    Map<Integer, Integer> getSaleOrderCount(int year);
}
