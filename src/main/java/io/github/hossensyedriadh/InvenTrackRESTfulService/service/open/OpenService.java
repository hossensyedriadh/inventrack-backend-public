package io.github.hossensyedriadh.InvenTrackRESTfulService.service.open;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.GenericStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SignupRequest;

public sealed interface OpenService permits OpenServiceImpl {
    Boolean isUsernameUnique(String username);

    Boolean isEmailUnique(String email);

    Boolean isTokenValid(String token);

    GenericStatus doSignup(SignupRequest signupRequest);

    GenericStatus requestPasswordReset(String username);

    Boolean checkOtp(String username, String otp);

    GenericStatus resetPassword(String id, String otp, String newPassword);
}
