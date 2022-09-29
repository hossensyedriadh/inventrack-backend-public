package io.github.hossensyedriadh.inventrackrestfulservice.service.purchase;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

public interface PurchaseService {
    Page<PurchaseOrder> purchaseOrders(Pageable pageable);

    Page<PurchaseOrder> purchaseOrders(Pageable pageable, String supplierPhone);

    PurchaseOrder purchaseOrder(String id);

    PurchaseOrder addPurchaseOrder(PurchaseOrder purchaseOrder);

    PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder, @Nullable String productId);

    PurchaseOrder createProductRestockOrder(PurchaseOrder purchaseOrder, String productId);
}
