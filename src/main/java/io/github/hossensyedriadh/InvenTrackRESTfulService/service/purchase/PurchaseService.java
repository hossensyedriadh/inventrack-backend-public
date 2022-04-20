package io.github.hossensyedriadh.InvenTrackRESTfulService.service.purchase;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PurchaseOrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.Optional;

public sealed interface PurchaseService permits PurchaseServiceImpl {
    Page<PurchaseOrderModel> purchaseOrders(Pageable pageable);

    Page<PurchaseOrderModel> purchaseOrders(Pageable pageable, String supplierPhone);

    Optional<PurchaseOrderModel> purchaseOrder(String id);

    Optional<PurchaseOrderModel> addPurchaseOrder(PurchaseOrderModel purchaseOrderModel);

    Optional<PurchaseOrderModel> updatePurchaseOrder(PurchaseOrderModel purchaseOrderModel, @Nullable String productId);

    Optional<PurchaseOrderModel> createProductRestockRequest(PurchaseOrderModel purchaseOrderModel, String productId);
}
