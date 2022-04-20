package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "PasswordChangeRequest", description = "Request body to change user's password")
@Getter
@Setter
public final class PasswordChangeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 2593930473311127028L;

    @ApiModelProperty(value = "Current password of the user in plain text", required = true)
    private String currentPassword;

    @ApiModelProperty(value = "New password in plain text", required = true)
    private String newPassword;
}
