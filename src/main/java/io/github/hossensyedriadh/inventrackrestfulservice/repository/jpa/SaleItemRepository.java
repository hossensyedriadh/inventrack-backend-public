package io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, String> {
    @Query(value = "select sum(quantity) from sale_items inner join sales s on sale_items.sale_ref = s.id where extract(year from added_on) = ?;", nativeQuery = true)
    Integer getUnitsSold(int year);

    @Query(value = "select sum(quantity) from sale_items inner join sales s on sale_items.sale_ref = s.id where extract(year from added_on) = ? and extract(month from added_on) = ?;", nativeQuery = true)
    Integer getUnitsSold(int year, int month);
}
