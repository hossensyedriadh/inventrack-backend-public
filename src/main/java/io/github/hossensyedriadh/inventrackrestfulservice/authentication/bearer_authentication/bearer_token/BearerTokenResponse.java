package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public final class BearerTokenResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -7598236061113730785L;

    private String access_token;

    private String access_token_type;

    private String refresh_token;
}
