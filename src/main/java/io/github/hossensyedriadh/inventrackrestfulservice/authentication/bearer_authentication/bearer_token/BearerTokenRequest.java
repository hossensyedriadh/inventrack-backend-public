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
public final class BearerTokenRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4212733349269089217L;

    private String username;

    private String passphrase;
}
