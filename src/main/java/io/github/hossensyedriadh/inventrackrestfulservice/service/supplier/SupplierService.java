package io.github.hossensyedriadh.inventrackrestfulservice.service.supplier;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {
    Page<Supplier> suppliers(Pageable pageable);

    List<Supplier> suppliers();

    Supplier supplier(String phone);

    Supplier add(Supplier supplier);

    Supplier update(Supplier supplier);
}
