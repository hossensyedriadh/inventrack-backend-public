package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "AccessTokenRequest", description = "Request body to get new Access token")
@NoArgsConstructor
@Getter
@Setter
public final class AccessTokenRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6283431620903067004L;

    @ApiModelProperty(value = "JWT Refresh Token", required = true)
    private String refresh_token;
}