package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Profile;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.ProfileModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProfileToProfileModel implements Converter<Profile, ProfileModel> {
    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public ProfileModel convert(Profile source) {
        ProfileModel profile = new ProfileModel();
        profile.setId(source.getId());
        profile.setFirstName(source.getFirstName());
        profile.setLastName(source.getLastName());
        profile.setEmail(source.getEmail());
        profile.setPhone((source.getPhoneNo() != null) ? source.getPhoneNo() : null);
        profile.setUserSince(source.getUserSince());
        profile.setAvatar((source.getAvatar() != null) ? source.getAvatar() : null);

        return profile;
    }
}
