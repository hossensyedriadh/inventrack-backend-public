package io.github.hossensyedriadh.inventrackrestfulservice.service.authentication;

import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.AccessTokenRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.BearerTokenRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.BearerTokenResponse;

public interface AuthenticationService {
    BearerTokenResponse authenticate(BearerTokenRequest bearerTokenRequest);

    BearerTokenResponse renewAccessToken(AccessTokenRequest accessTokenRequest);
}
