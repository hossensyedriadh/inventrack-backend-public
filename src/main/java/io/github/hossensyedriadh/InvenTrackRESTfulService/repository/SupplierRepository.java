package io.github.hossensyedriadh.InvenTrackRESTfulService.repository;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, String>, PagingAndSortingRepository<Supplier, String>, JpaSpecificationExecutor<Supplier> {

}
