package io.github.hossensyedriadh.InvenTrackRESTfulService.service.invitations;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupInvitationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public sealed interface InvitationService permits InvitationServiceImpl {
    Page<SignupInvitationModel> invitations(Pageable pageable);

    Optional<SignupInvitationModel> invitation(String id);

    Optional<SignupInvitationModel> createInvitation(SignupInvitationModel signupInvitationModel);

    Optional<SignupInvitationModel> invalidateInvitation(String id);
}
