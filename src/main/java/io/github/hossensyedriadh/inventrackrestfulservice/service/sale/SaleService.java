package io.github.hossensyedriadh.inventrackrestfulservice.service.sale;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SaleService {
    Page<Sale> saleOrders(Pageable pageable);

    Page<Sale> saleOrders(Pageable pageable, String customerPhone);

    Sale sale(String id);

    Sale add(Sale sale);

    Sale update(Sale sale);
}
