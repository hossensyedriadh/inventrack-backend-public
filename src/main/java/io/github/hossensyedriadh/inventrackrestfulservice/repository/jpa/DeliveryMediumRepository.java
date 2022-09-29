package io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.DeliveryMedium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryMediumRepository extends JpaRepository<DeliveryMedium, String>, JpaSpecificationExecutor<DeliveryMedium> {
}
