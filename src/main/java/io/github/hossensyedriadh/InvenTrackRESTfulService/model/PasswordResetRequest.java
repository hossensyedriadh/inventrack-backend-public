package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "PasswordResetRequest",
        description = "Request body to get OTP through email by finding user by given username, email")
@Getter
@Setter
public final class PasswordResetRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 6900975181231909813L;

    @ApiModelProperty(value = "Username/email of the user", required = true)
    private String id;

    @ApiModelProperty(value = "OTP received through email", required = true)
    private String otp;
}
