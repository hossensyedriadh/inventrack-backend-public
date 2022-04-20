package io.github.hossensyedriadh.InvenTrackRESTfulService.service.sale;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SaleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public sealed interface SaleService permits SaleServiceImpl {
    Page<SaleModel> getSales(Pageable pageable);

    Page<SaleModel> getSales(Pageable pageable, String customerPhone);

    Optional<SaleModel> getSale(String id);

    Optional<SaleModel> addSale(SaleModel saleModel);

    Optional<SaleModel> updateSale(SaleModel saleModel);
}
