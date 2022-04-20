package io.github.hossensyedriadh.InvenTrackRESTfulService.repository;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, PagingAndSortingRepository<PurchaseOrder, String>,
        JpaSpecificationExecutor<PurchaseOrder> {
    Page<PurchaseOrder> findPurchaseOrdersBySupplierPhoneNo(Pageable pageable, String supplierPhoneNo);

    @Query(value = "select count(*) from purchase_orders where extract(year from added_on) = ?;", nativeQuery = true)
    Integer getPurchaseOrderCountByYear(int year);

    @Query(value = "select count(*) from purchase_orders where extract(month from added_on) = ?;", nativeQuery = true)
    Integer getPurchaseOrderCountByMonth(int month);
}
