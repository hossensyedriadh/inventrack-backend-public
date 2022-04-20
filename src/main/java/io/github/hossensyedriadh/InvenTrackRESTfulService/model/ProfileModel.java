package io.github.hossensyedriadh.InvenTrackRESTfulService.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ApiModel(value = "Profile", description = "Profile model representing fields for both update and fetch operations")
@Getter
@Setter
public final class ProfileModel implements Serializable {
    @Serial
    private static final long serialVersionUID = -1152638985078964432L;

    @ApiModelProperty(value = "ID of the profile", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String id;

    @ApiModelProperty(value = "First name of the user", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String firstName;

    @ApiModelProperty(value = "Last name of the user", required = true,
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String lastName;

    @ApiModelProperty(value = "Email of the user, must be unique", required = true,
            accessMode = ApiModelProperty.AccessMode.READ_WRITE)
    private String email;

    @ApiModelProperty(value = "Phone no. of the user",
            accessMode = ApiModelProperty.AccessMode.AUTO)
    private String phone;

    @ApiModelProperty(value = "Date of when the user signed up",
            accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private Date userSince;

    @ApiModelProperty(value = "Static URL of the avatar image", accessMode = ApiModelProperty.AccessMode.AUTO,
            example = "https://cdn.test.com/users/images/821be4ba004294c7.png")
    private String avatar;
}
