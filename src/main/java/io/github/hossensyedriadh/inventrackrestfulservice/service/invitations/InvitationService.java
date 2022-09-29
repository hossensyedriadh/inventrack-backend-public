package io.github.hossensyedriadh.inventrackrestfulservice.service.invitations;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.SignupInvitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvitationService {
    Page<SignupInvitation> invitations(Pageable pageable);

    SignupInvitation invitation(String id);

    SignupInvitation create(SignupInvitation signupInvitation);

    SignupInvitation invalidate(String id);
}
