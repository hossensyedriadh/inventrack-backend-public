package io.github.hossensyedriadh.inventrackrestfulservice.service.open;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordResetBody;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PasswordResetRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.model.SignupRequest;

public interface OpenService {
    Boolean isUsernameUnique(String username);

    Boolean isEmailUnique(String email);

    Boolean isInvitationTokenValid(String token);

    User signUp(SignupRequest signupRequest);

    void requestPasswordReset(String username);

    Boolean checkOtp(PasswordResetRequest resetRequest);

    void resetPassword(PasswordResetBody passwordResetBody);
}
