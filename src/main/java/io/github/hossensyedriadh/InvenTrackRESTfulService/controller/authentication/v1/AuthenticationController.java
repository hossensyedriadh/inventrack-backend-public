package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.authentication.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt.AccessTokenRequest;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt.JwtRequest;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.jwt.JwtResponse;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.service.JwtService;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.InvalidCredentialsException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.InvalidRefreshTokenException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.UserAccountLockedException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(value = "/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.security.jwt.token-type}")
    private String tokenType;

    @Autowired
    public AuthenticationController(JwtService jwtService, UserDetailsService userDetailsService,
                                    PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhY2Nlc3NfdG9rZW4iLCJhdWQiOiJpbnZlb
                                    nRyYWNrIiwibmJmIjoxNjQxNjU0MTYyLCJhdXRob3JpdHkiOiJST0xFX0FETUlOSVNUUkFUT1IiLCJpc3MiOiJJbnZlblRyYWNr
                                    IiwiZXhwIjoxNjQxNjYxMzYyLCJpYXQiOjE2NDE2NTQxNjIsInVzZXJuYW1lIjoic3llZHJpYWRoaG9zc2VuIn0.T1acZXFkO-_
                                    w7P_Kj6rvmzMdn6cc_kUGFi3AuuXANl1Q3FSG-cQ57NzuQCkekAmaBbYw609RZO8EnhpvLo6HnA6ZArxSBtmdUKMgZJTeNy-OYO
                                    TeyTaMkOtySxuKVkrws73n2X_OAAOWPdcvHrjb7rT9ThiIEi_qG4UMRHF2VvygWc5Na6CJFwA6CWJOah-Jy_GBL6XxTwl5lzHLy
                                    u153Y2wdNnjAfsnN18jWoxbrmaKolvSGyD79tVquKuHDCmgfq2K-VDm8HJbpPXVXS7UvRGjNfoxiq7rPzrt2XVaWs_pOuT6KStK
                                    FN8pKCJapRskkBSlX7wLgInJCrg_puTRsw",
                                "refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJyZWZyZXNoX3Rva2VuIiwiYXVkIjoiaW52
                                    ZW50cmFjayIsIm5iZiI6MTY0MTA1NjM3MywiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTklTVFJBVE9SIiwiaXNzIjoiSW52ZW5UcmF
                                    jayIsImV4cCI6MTY0MTY2MTE3MywiaWF0IjoxNjQxMDU2MzczLCJqdGkiOiIzZGE0Nzg4OS1kNThjLTQxODYtOGRhNi02MWRlND
                                    gwMDllODkiLCJ1c2VybmFtZSI6InN5ZWRyaWFkaGhvc3NlbiJ9.SoxCM9RJbCWj1QVMhTZAFnwG0boNAAxRK-tSVinkqv2QkqfY
                                    vB1q0xVtSvfmjwRppf1i-DEH_mpnpGiYPVZXQw1nMtumSt4ZPV5Vbj2x7DuUlD7MSkZZCKRPRpq-8CUMqVsrSZNUDBoKXh7q2nD
                                    eZsLLWjkgRJJ5wSt_jq1PRCNMft_V0VbO2JjZDEEUNuPH0amMfPgpvDpYV6InFVlvgZ3mkZYkl039L_Fj-gUOJXg2swDHFz1H3q
                                    MTfNAwOQgu_rCvYaS5zv9wcIaZnWZ0eC8o2lrgXIeXOr5FR2MithuroxzVmsyuRBY-aYgfVTMFfflhB0k2Q_zNc4o0tJSRiA",
                                "token_type": "Bearer"
                            }
                            """)
            })),
            @ApiResponse(code = 401, message = "Unauthorized", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 401,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invalid credentials",
                                "error": "Unauthorized",
                                "path": "/api/v1/authentication/authenticate"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Authenticate using username/email and password",
            description = "Returns access token, refresh token and token type when correct username/email and password is passed in the body")
    @PostMapping(value = "/authenticate")
    public JwtResponse authenticate(HttpServletRequest request, HttpServletResponse response, @RequestBody JwtRequest jwtRequest) {
        UserDetails userDetails;

        try {
            userDetails = userDetailsService.loadUserByUsername(jwtRequest.getId());
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found: " + jwtRequest.getId());
        } catch (UserAccountLockedException e) {
            throw new UserAccountLockedException("Account is locked: " + jwtRequest.getId());
        }

        if (passwordEncoder.matches(jwtRequest.getPassphrase(), userDetails.getPassword())) {
            Map<String, String> claims = new HashMap<>();
            claims.put("username", userDetails.getUsername());
            claims.put("authority", String.valueOf(userDetails.getAuthorities().toArray()[0]));

            String accessToken = jwtService.getAccessToken(claims);
            String refreshToken = jwtService.getRefreshToken(userDetails.getUsername(), claims);

            response.addHeader(HttpHeaders.EXPIRES, String.valueOf(LocalDateTime.ofInstant(
                    Objects.requireNonNull(jwtService.jwtDecoder().decode(accessToken).getExpiresAt()),
                    ZoneId.of("Asia/Dhaka"))));
            response.setStatus(HttpServletResponse.SC_OK);
            return new JwtResponse(accessToken, refreshToken, tokenType);
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        throw new InvalidCredentialsException("Invalid credentials", request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhY2Nlc3NfdG9rZW4iLCJhdWQiOiJpbnZlb
                                    nRyYWNrIiwibmJmIjoxNjQxNjU0MTYyLCJhdXRob3JpdHkiOiJST0xFX0FETUlOSVNUUkFUT1IiLCJpc3MiOiJJbnZlblRyYWNr
                                    IiwiZXhwIjoxNjQxNjYxMzYyLCJpYXQiOjE2NDE2NTQxNjIsInVzZXJuYW1lIjoic3llZHJpYWRoaG9zc2VuIn0.T1acZXFkO-_
                                    w7P_Kj6rvmzMdn6cc_kUGFi3AuuXANl1Q3FSG-cQ57NzuQCkekAmaBbYw609RZO8EnhpvLo6HnA6ZArxSBtmdUKMgZJTeNy-OYO
                                    TeyTaMkOtySxuKVkrws73n2X_OAAOWPdcvHrjb7rT9ThiIEi_qG4UMRHF2VvygWc5Na6CJFwA6CWJOah-Jy_GBL6XxTwl5lzHLy
                                    u153Y2wdNnjAfsnN18jWoxbrmaKolvSGyD79tVquKuHDCmgfq2K-VDm8HJbpPXVXS7UvRGjNfoxiq7rPzrt2XVaWs_pOuT6KStK
                                    FN8pKCJapRskkBSlX7wLgInJCrg_puTRsw",
                                "refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJyZWZyZXNoX3Rva2VuIiwiYXVkIjoiaW52
                                    ZW50cmFjayIsIm5iZiI6MTY0MTA1NjM3MywiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTklTVFJBVE9SIiwiaXNzIjoiSW52ZW5UcmF
                                    jayIsImV4cCI6MTY0MTY2MTE3MywiaWF0IjoxNjQxMDU2MzczLCJqdGkiOiIzZGE0Nzg4OS1kNThjLTQxODYtOGRhNi02MWRlND
                                    gwMDllODkiLCJ1c2VybmFtZSI6InN5ZWRyaWFkaGhvc3NlbiJ9.SoxCM9RJbCWj1QVMhTZAFnwG0boNAAxRK-tSVinkqv2QkqfY
                                    vB1q0xVtSvfmjwRppf1i-DEH_mpnpGiYPVZXQw1nMtumSt4ZPV5Vbj2x7DuUlD7MSkZZCKRPRpq-8CUMqVsrSZNUDBoKXh7q2nD
                                    eZsLLWjkgRJJ5wSt_jq1PRCNMft_V0VbO2JjZDEEUNuPH0amMfPgpvDpYV6InFVlvgZ3mkZYkl039L_Fj-gUOJXg2swDHFz1H3q
                                    MTfNAwOQgu_rCvYaS5zv9wcIaZnWZ0eC8o2lrgXIeXOr5FR2MithuroxzVmsyuRBY-aYgfVTMFfflhB0k2Q_zNc4o0tJSRiA",
                                "token_type": "Bearer"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Invalid refresh token",
                                "error": "Bad Request",
                                "path": "/api/v1/authentication/access-token"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Get access token", description = "Returns new access token when valid refresh token is passed in the body")
    @PostMapping(value = "/access-token")
    public ResponseEntity<JwtResponse> authenticate(HttpServletRequest request, HttpServletResponse response,
                                                    @RequestBody AccessTokenRequest accessTokenRequest) {
        String receivedRefreshToken = accessTokenRequest.getRefresh_token();

        boolean isValid = jwtService.isRefreshTokenValid(receivedRefreshToken);

        if (isValid) {
            Jwt jwt = jwtService.jwtDecoder().decode(receivedRefreshToken);

            Map<String, Object> claims = jwt.getClaims();
            Map<String, String> convertedClaims = new HashMap<>();

            convertedClaims.put("username", claims.get("username").toString());
            convertedClaims.put("authority", claims.get("authority").toString());

            String accessToken = jwtService.getAccessToken(convertedClaims);

            response.addHeader(HttpHeaders.EXPIRES, String.valueOf(LocalDateTime.ofInstant(
                    Objects.requireNonNull(jwtService.jwtDecoder().decode(accessToken).getExpiresAt()),
                    ZoneId.of("Asia/Dhaka"))));

            return new ResponseEntity<>(new JwtResponse(accessToken, receivedRefreshToken, tokenType), HttpStatus.OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new InvalidRefreshTokenException("Invalid refresh token", request.getRequestURI());
        }
    }
}
