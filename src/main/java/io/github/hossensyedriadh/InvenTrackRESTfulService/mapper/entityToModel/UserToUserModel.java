package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserModel implements Converter<User, UserModel> {
    private final ProfileToProfileModel profileConverter;

    @Autowired
    public UserToUserModel(ProfileToProfileModel profileConverter) {
        this.profileConverter = profileConverter;
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public UserModel convert(User source) {
        UserModel user = new UserModel();
        user.setUsername(source.getUsername());
        user.setEnabled(source.isEnabled());
        user.setAccountNotLocked(source.isAccountNotLocked());
        user.setAuthority(source.getAuthority());
        user.setProfile(profileConverter.convert(source.getProfile()));

        return user;
    }
}
