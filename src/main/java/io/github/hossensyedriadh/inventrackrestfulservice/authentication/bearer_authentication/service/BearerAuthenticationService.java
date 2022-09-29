package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.RefreshToken;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.User;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.RefreshTokenRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.UserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Log4j
@Service
public class BearerAuthenticationService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;

    @Autowired
    public BearerAuthenticationService(RefreshTokenRepository refreshTokenRepository,
                                       UserRepository userRepository, RSAPublicKey rsaPublicKey,
                                       RSAPrivateKey rsaPrivateKey) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    private final String tokenIssuer = "https://inventrack-restful-service.herokuapp.com";

    private int accessTokenValidity;

    private final String accessTokenSubject = "Access Token";

    private int refreshTokenValidity;

    private final String refreshTokenSubject = "Refresh Token";

    @Value("${bearer-authentication.token.audience}")
    private String audience;

    @Value("${bearer-authentication.token.access-token.validity-mins}")
    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    @Value("${bearer-authentication.token.refresh-token.validity-hours}")
    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String generateAccessToken(Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.MINUTE, this.accessTokenValidity);

        JWTCreator.Builder accessTokenBuilder = JWT.create().withSubject(this.accessTokenSubject).withIssuer(this.tokenIssuer)
                .withAudience(this.audience);
        claims.forEach(accessTokenBuilder::withClaim);

        return accessTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey));
    }

    public Boolean isAccessTokenValid(String token, UserDetails userDetails) {
        Jwt decodedJwt = NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build().decode(token);

        String username = decodedJwt.getClaimAsString("username");
        String issuer = decodedJwt.getClaimAsString("iss");
        String subject = decodedJwt.getSubject();
        String audience = decodedJwt.getAudience() != null ? decodedJwt.getAudience().get(0) : "";

        return username.equals(userDetails.getUsername()) && issuer.equals(this.tokenIssuer)
                && subject.equals(this.accessTokenSubject) && audience.equals(this.audience)
                && Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(Instant.now());
    }

    public String getRefreshToken(String username, Map<String, String> claims) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll()
                .stream().filter(token -> token.getForUser().getUsername().equals(username)).toList();

        if (refreshTokens.size() == 1) {
            RefreshToken token = refreshTokens.get(0);

            try {
                Jwt jwt = NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build().decode(token.getToken());

                String audience = jwt.getAudience() != null ? jwt.getAudience().get(0) : "";

                if (Objects.requireNonNull(jwt.getExpiresAt()).isAfter(Instant.now()) && jwt.getSubject().equals(this.refreshTokenSubject)
                        && jwt.getClaimAsString("iss").equals(this.tokenIssuer) && audience.equals(this.audience)) {
                    return token.getToken();
                }

                return this.createRefreshToken(username, claims);
            } catch (JwtValidationException e) {
                log.warn("Invalid refresh token received, generating new token...");
                refreshTokenRepository.delete(token);
                return this.createRefreshToken(username, claims);
            }
        }

        return this.createRefreshToken(username, claims);
    }

    public Boolean isRefreshTokenValid(String refreshToken) {
        try {
            Jwt decodedJwt = NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build().decode(refreshToken);

            List<RefreshToken> tokens = refreshTokenRepository.findAll().stream()
                    .filter(token -> token.getForUser().getUsername().equals(decodedJwt.getClaimAsString("username"))).toList();

            if (tokens.size() == 1 && decodedJwt != null) {
                RefreshToken token = tokens.get(0);

                if (token.getToken().equals(decodedJwt.getTokenValue())) {
                    String audience = decodedJwt.getAudience() != null ? decodedJwt.getAudience().get(0) : "";

                    return Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(Instant.now())
                            && decodedJwt.getSubject().equals(this.refreshTokenSubject)
                            && decodedJwt.getClaimAsString("iss").equals(this.tokenIssuer)
                            && audience.equals(this.audience);
                }
            }

            return false;
        } catch (Exception e) {
            log.error(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            return false;
        }
    }

    private User getUser(String username) {
        if (userRepository.findById(username).isPresent()) {
            return userRepository.findById(username).get();
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }

    private String createRefreshToken(String username, Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.HOUR, this.refreshTokenValidity);

        JWTCreator.Builder refreshTokenBuilder = JWT.create().withSubject(this.refreshTokenSubject)
                .withIssuer(this.tokenIssuer).withAudience(this.audience);
        claims.forEach(refreshTokenBuilder::withClaim);

        String id = UUID.randomUUID().toString();

        String token = refreshTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).withJWTId(id).sign(Algorithm.RSA256(this.rsaPublicKey, this.rsaPrivateKey));

        this.persistRefreshToken(username, id, token);

        return token;
    }

    @Async
    protected void persistRefreshToken(String username, String jwtId, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(jwtId);
        refreshToken.setToken(token);
        refreshToken.setForUser(this.getUser(username));

        CompletableFuture<RefreshToken> completableFuture = new CompletableFuture<>();
        Executors.newSingleThreadExecutor().submit(() -> {
            completableFuture.completeAsync(() -> refreshTokenRepository.saveAndFlush(refreshToken));
        });
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.rsaPublicKey).build();
    }
}
