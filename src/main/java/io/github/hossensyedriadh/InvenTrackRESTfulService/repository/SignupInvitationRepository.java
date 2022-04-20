package io.github.hossensyedriadh.InvenTrackRESTfulService.repository;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SignupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupInvitationRepository extends JpaRepository<SignupInvitation, String>,
        PagingAndSortingRepository<SignupInvitation, String>, JpaSpecificationExecutor<SignupInvitation> {

}
