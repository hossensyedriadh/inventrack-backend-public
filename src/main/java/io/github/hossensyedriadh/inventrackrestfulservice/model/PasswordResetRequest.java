package io.github.hossensyedriadh.inventrackrestfulservice.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class PasswordResetRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 9196138587530020476L;

    private String id;

    private String otp;
}
