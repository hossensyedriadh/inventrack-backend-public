package io.github.hossensyedriadh.InvenTrackRESTfulService.service.count;

public sealed interface CountService permits CountServiceImpl {
    Double getTotalSales();

    Double getTotalCost();

    Integer getProductsSold();

    Double getStockAvailable();
}
