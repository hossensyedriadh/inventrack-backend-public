package io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String>, PagingAndSortingRepository<Sale, String>,
        JpaSpecificationExecutor<Sale> {
    Page<Sale> findSalesByCustomerPhoneNo(Pageable pageable, String customerPhoneNo);

    @Query(value = "select count(*) from sales where extract(year from added_on) = ?;", nativeQuery = true)
    Integer getSaleOrderCount(int year);

    @Query(value = "select count(*) from sales where extract(year from added_on) = ? and extract(month from added_on) = ?;", nativeQuery = true)
    Integer getSaleOrderCount(int year, int month);
}
