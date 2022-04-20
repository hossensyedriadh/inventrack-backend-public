package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "PasswordBody", description = "Request body to check if password is valid")
@Getter
@Setter
public final class PasswordBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 5285565842731018352L;

    @ApiModelProperty(value = "Password of the user in plain text", required = true)
    private String password;
}
