package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(value = "JwtResponse", description = "Response body containing authentication tokens when successfully authenticated")
@AllArgsConstructor
@Getter
@Setter
public final class JwtResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 5384954745819497620L;

    @ApiModelProperty(value = "JWT Access Token")
    private String access_token;

    @ApiModelProperty(value = "JWT Refresh Token")
    private String refresh_token;

    @ApiModelProperty(value = "Type of the tokens")
    private String token_type;
}
