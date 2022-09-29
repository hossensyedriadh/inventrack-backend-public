package io.github.hossensyedriadh.inventrackrestfulservice.service.authentication;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.AccessTokenRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.BearerTokenRequest;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.bearer_token.BearerTokenResponse;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service.BearerAuthenticationService;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service.BearerAuthenticationUserDetailsService;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.InvalidCredentialsException;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.InvalidRefreshTokenException;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.UserAccountLockedException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final BearerAuthenticationService bearerAuthenticationService;
    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private LoadingCache<String, String> accessTokenCache;

    private int accessTokenValidityMins;

    @Autowired
    public AuthenticationServiceImpl(BearerAuthenticationService bearerAuthenticationService,
                                     BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService,
                                     PasswordEncoder passwordEncoder, UserRepository userRepository,
                                     HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
    }

    @Value("${bearer-authentication.token.access-token.validity-mins}")
    public void setAccessTokenValidityMins(int accessTokenValidityMins) {
        this.accessTokenValidityMins = accessTokenValidityMins;
    }

    @PostConstruct
    private void initializeCache() {
        this.accessTokenCache = CacheBuilder.newBuilder().expireAfterWrite(this.accessTokenValidityMins, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    @NonNull
                    public String load(@NonNull String key) {
                        return "";
                    }
                });
    }

    @Value("${bearer-authentication.token.access-token.type}")
    private String accessTokenType;

    @Override
    public BearerTokenResponse authenticate(BearerTokenRequest bearerTokenRequest) {
        UserDetails userDetails;

        try {
            userDetails = this.bearerAuthenticationUserDetailsService.loadUserByUsername(bearerTokenRequest.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (UserAccountLockedException e) {
            throw new UserAccountLockedException(e.getMessage());
        }

        if (this.passwordEncoder.matches(bearerTokenRequest.getPassphrase(), userDetails.getPassword())) {
            try {
                if (this.accessTokenCache.asMap().containsKey(userDetails.getUsername())) {
                    String existingToken = this.accessTokenCache.get(userDetails.getUsername());

                    if (this.bearerAuthenticationService.isAccessTokenValid(existingToken, userDetails)) {
                        Map<String, String> claims = new HashMap<>();
                        claims.put("username", userDetails.getUsername());

                        String refreshToken = this.bearerAuthenticationService.getRefreshToken(userDetails.getUsername(), claims);

                        httpServletResponse.addHeader(HttpHeaders.EXPIRES, String.valueOf(LocalDateTime.ofInstant(
                                Objects.requireNonNull(this.bearerAuthenticationService.jwtDecoder().decode(existingToken).getExpiresAt()),
                                ZoneId.systemDefault())));
                        return new BearerTokenResponse(existingToken, this.accessTokenType, refreshToken);
                    }
                } else {
                    String username = bearerTokenRequest.getUsername();
                    boolean isUsername = username.matches("^[a-zA-Z_]{4,75}$");
                    boolean isEmail = username.matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");

                    User user;

                    if (isUsername) {
                        if (userRepository.findById(username).isPresent()) {
                            user = userRepository.findById(username).get();
                        } else {
                            throw new UsernameNotFoundException("User not found: " + username);
                        }
                    } else if (isEmail) {
                        if (userRepository.findAll().stream().anyMatch(usr -> usr.getProfile().getEmail().equals(username))) {
                            user = userRepository.findAll().stream().filter(usr -> usr.getProfile().getEmail().equals(username)).toList().get(0);
                        } else {
                            throw new UsernameNotFoundException("User not found: " + username);
                        }
                    } else {
                        throw new UsernameNotFoundException("User not found: " + username);
                    }

                    Map<String, String> claims = new HashMap<>();
                    claims.put("username", userDetails.getUsername());

                    String accessToken = this.bearerAuthenticationService.generateAccessToken(claims);
                    String refreshToken = this.bearerAuthenticationService.getRefreshToken(user.getUsername(), claims);

                    this.accessTokenCache.invalidate(userDetails.getUsername());
                    this.accessTokenCache.put(userDetails.getUsername(), accessToken);

                    httpServletResponse.addHeader(HttpHeaders.EXPIRES, String.valueOf(LocalDateTime.ofInstant(
                            Objects.requireNonNull(this.bearerAuthenticationService.jwtDecoder().decode(accessToken).getExpiresAt()),
                            ZoneId.systemDefault())));

                    return new BearerTokenResponse(accessToken, this.accessTokenType, refreshToken);
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        throw new InvalidCredentialsException("Invalid credentials", httpServletRequest);
    }

    @Override
    public BearerTokenResponse renewAccessToken(AccessTokenRequest accessTokenRequest) {
        String refreshToken = accessTokenRequest.getRefresh_token();
        boolean isRefreshTokenValid = this.bearerAuthenticationService.isRefreshTokenValid(refreshToken);

        if (isRefreshTokenValid) {
            String username;
            try {
                username = this.bearerAuthenticationService.jwtDecoder().decode(refreshToken).getClaim("username");
            } catch (JwtValidationException e) {
                throw new InvalidRefreshTokenException("Invalid refresh token", httpServletRequest);
            }

            if (this.accessTokenCache.asMap().containsKey(username)) {
                try {
                    String accessToken = this.accessTokenCache.get(username);
                    boolean isAccessTokenValid = this.bearerAuthenticationService.isAccessTokenValid(accessToken,
                            this.bearerAuthenticationUserDetailsService.loadUserByUsername(username));

                    if (isAccessTokenValid) {
                        return new BearerTokenResponse(accessToken, this.accessTokenType, refreshToken);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    log.error(e);
                }
            }

            Jwt jwt = this.bearerAuthenticationService.jwtDecoder().decode(refreshToken);

            Map<String, Object> claims = jwt.getClaims();
            Map<String, String> convertedClaims = new HashMap<>();

            convertedClaims.put("username", claims.get("username").toString());

            String accessToken = this.bearerAuthenticationService.generateAccessToken(convertedClaims);

            this.accessTokenCache.invalidate(username);
            this.accessTokenCache.put(username, accessToken);

            httpServletResponse.addHeader(HttpHeaders.EXPIRES, String.valueOf(LocalDateTime.ofInstant(
                    Objects.requireNonNull(this.bearerAuthenticationService.jwtDecoder().decode(accessToken).getExpiresAt()),
                    ZoneId.systemDefault())));

            return new BearerTokenResponse(accessToken, this.accessTokenType, refreshToken);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new InvalidRefreshTokenException("Invalid refresh token", httpServletRequest);
        }
    }
}
