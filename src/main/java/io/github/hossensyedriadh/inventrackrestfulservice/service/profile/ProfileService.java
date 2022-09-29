package io.github.hossensyedriadh.inventrackrestfulservice.service.profile;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordChangeRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    User getUser();

    Boolean isPasswordValid(String password);

    User updateProfile(User user);

    void changePassword(PasswordChangeRequest passwordChangeRequest);

    void changeAvatar(MultipartFile file);
}
