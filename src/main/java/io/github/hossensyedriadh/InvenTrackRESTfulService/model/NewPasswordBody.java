package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "NewPasswordBody", description = "Request body to reset password")
@Getter
@Setter
public final class NewPasswordBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 6198317539005668536L;

    @ApiModelProperty(value = "Username/email of the user", required = true)
    private String id;

    @ApiModelProperty(value = "OTP received in through email for password reset", required = true)
    private String otp;

    @ApiModelProperty(value = "New password in plain text format", required = true,
            example = "password")
    private String newPassword;
}
