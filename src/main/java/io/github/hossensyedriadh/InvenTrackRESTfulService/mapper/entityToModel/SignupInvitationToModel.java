package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SignupInvitation;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupInvitationModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SignupInvitationToModel implements Converter<SignupInvitation, SignupInvitationModel> {
    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public SignupInvitationModel convert(SignupInvitation source) {
        SignupInvitationModel model = new SignupInvitationModel();
        model.setId(source.getId());
        model.setCreatedOn(source.getCreatedOn());
        model.setExpiresOn(source.getExpiresOn());
        model.setStatus(source.getStatus());
        model.setInvalidationRemarks((source.getInvalidationRemarks() != null) ? source.getInvalidationRemarks() : null);
        model.setInvalidatedOn((source.getInvalidatedOn() != null) ? source.getInvalidatedOn() : null);
        model.setRecipientEmail(source.getRecipientEmail());
        model.setForAuthority(source.getForAuthority());
        model.setCreatedBy(source.getCreatedBy().getUsername());

        return model;
    }
}
