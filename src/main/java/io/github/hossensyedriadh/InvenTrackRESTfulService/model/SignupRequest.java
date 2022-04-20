package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "SignupRequest", description = "Request body containing fields to signup")
@Getter
@Setter
public final class SignupRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 4396821620043803138L;

    @ApiModelProperty(value = "Token of the invitation", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String token;

    @ApiModelProperty(value = "Username of the user (must be unique)", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String username;

    @ApiModelProperty(value = "Password of the user in plain text", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String passphrase;

    @ApiModelProperty(value = "First name of the user", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String firstName;

    @ApiModelProperty(value = "Last name of the user", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String lastName;

    @ApiModelProperty(value = "Phone no. of the user",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String phone;

    @ApiModelProperty(value = "Static URL of the avatar image",
            accessMode = ApiModelProperty.AccessMode.AUTO,
            example = "https://cdn.test.com/users/images/821be4ba004294c7.png")
    private String avatar;
}
