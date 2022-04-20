package io.github.hossensyedriadh.InvenTrackRESTfulService.service.supplier;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SupplierModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public sealed interface SupplierService permits SupplierServiceImpl {
    Page<SupplierModel> getSuppliers(Pageable pageable);

    List<SupplierModel> getSuppliers();

    Optional<SupplierModel> getSupplier(String phone);

    Optional<SupplierModel> addSupplier(SupplierModel supplierModel);

    Optional<SupplierModel> updateSupplier(SupplierModel supplierModel);
}
