package io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String>, PagingAndSortingRepository<Profile, String>,
        JpaSpecificationExecutor<Profile> {
}
