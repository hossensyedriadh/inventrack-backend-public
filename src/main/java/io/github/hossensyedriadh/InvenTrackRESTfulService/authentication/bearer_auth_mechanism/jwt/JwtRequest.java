package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "JwtRequest", description = "Request body to authenticate and get tokens as defined in JwtResponse")
@Data
public final class JwtRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -5628195373018428785L;

    @ApiModelProperty(value = "Username/email of the user", required = true)
    @NonNull
    private String id;

    @ApiModelProperty(value = "Password of the user in plain text", required = true)
    @NonNull
    private String passphrase;
}
