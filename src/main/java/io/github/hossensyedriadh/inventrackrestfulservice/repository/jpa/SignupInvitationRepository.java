package io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.SignupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignupInvitationRepository extends JpaRepository<SignupInvitation, String>, PagingAndSortingRepository<SignupInvitation, String> {
}
