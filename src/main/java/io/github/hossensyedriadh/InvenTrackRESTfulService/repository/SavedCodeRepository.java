package io.github.hossensyedriadh.InvenTrackRESTfulService.repository;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SavedCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedCodeRepository extends JpaRepository<SavedCode, String> {
}
