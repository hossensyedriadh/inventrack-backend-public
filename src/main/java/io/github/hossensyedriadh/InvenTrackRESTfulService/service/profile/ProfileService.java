package io.github.hossensyedriadh.InvenTrackRESTfulService.service.profile;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.GenericStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.UserModel;
import org.springframework.web.multipart.MultipartFile;

public sealed interface ProfileService permits ProfileServiceImpl {
    UserModel getUser();

    Boolean isPasswordValid(String password);

    UserModel updateProfile(UserModel userModel);

    GenericStatus changePassword(String currentPassword, String newPassword);

    GenericStatus changeAvatar(MultipartFile file);
}
