package io.github.hossensyedriadh.InvenTrackRESTfulService.repository;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.FinanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceRepository extends JpaRepository<FinanceRecord, Integer>,
        PagingAndSortingRepository<FinanceRecord, Integer>,
        JpaSpecificationExecutor<FinanceRecord> {
    @Query("select sum(f.value) as expenses_by_year from FinanceRecord f where f.type='EXPENSE' and f.year = ?1")
    Double getExpensesByYear(int year);

    @Query("select sum(f.value) as expenses_by_year from FinanceRecord f where f.type='EXPENSE' and f.month = ?1")
    Double getExpensesByMonth(int month);

    @Query("select sum(f.value) as sales_by_year from FinanceRecord f where f.type ='SALE' and f.year = ?1")
    Double getSalesByYear(int year);

    @Query("select sum(f.value) as sales_by_year from FinanceRecord f where f.type ='SALE' and f.month = ?1")
    Double getSalesByMonth(int month);
}
