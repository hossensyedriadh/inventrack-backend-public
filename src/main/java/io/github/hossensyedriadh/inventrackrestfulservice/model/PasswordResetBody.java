package io.github.hossensyedriadh.inventrackrestfulservice.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class PasswordResetBody implements Serializable {
    @Serial
    private static final long serialVersionUID = -8759880248940422612L;

    private String id;

    private String otp;

    private String newPassword;
}
