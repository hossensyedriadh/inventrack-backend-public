package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.Authority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "User", description = "User model representing fields for both update and fetch operations")
@Getter
@Setter
public final class UserModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -7590722162936397846L;

    @ApiModelProperty(value = "Username of the user", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String username;

    @ApiModelProperty(value = "Flag to define access of user",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private boolean enabled;

    @ApiModelProperty(value = "Flag to define account lock status of user",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private boolean accountNotLocked;

    @ApiModelProperty(value = "Authority of the user", allowableValues = "ROLE_ROOT, ROLE_ADMINISTRATOR, ROLE_MODERATOR",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Authority authority;

    @ApiModelProperty(value = "Profile details of the user", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private ProfileModel profile;
}
