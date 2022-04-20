package io.github.hossensyedriadh.InvenTrackRESTfulService.repository;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>, JpaSpecificationExecutor<RefreshToken> {

}
