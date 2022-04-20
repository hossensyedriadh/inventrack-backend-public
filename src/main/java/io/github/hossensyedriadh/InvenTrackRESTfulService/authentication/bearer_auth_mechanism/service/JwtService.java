package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.RefreshToken;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.User;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.RefreshTokenRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.UserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

@Log4j
@Service
public class JwtService {
    private final ObjectFactory<RefreshTokenRepository> refreshTokenRepositoryObjectFactory;
    private final ObjectFactory<UserRepository> userRepositoryObjectFactory;

    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;

    @Value("${spring.application.name}")
    private String jwtIssuer;

    private int accessTokenValidity;

    @Value("${application.security.jwt.access-token.subject}")
    private String accessTokenSubject;

    private int refreshTokenValidity;

    @Value("${application.security.jwt.refresh-token.subject}")
    private String refreshTokenSubject;

    @Value("${application.security.jwt.audience}")
    private String audience;

    @Autowired
    public JwtService(ObjectFactory<RefreshTokenRepository> refreshTokenRepositoryObjectFactory,
                      ObjectFactory<UserRepository> userRepositoryObjectFactory,
                      RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        this.refreshTokenRepositoryObjectFactory = refreshTokenRepositoryObjectFactory;
        this.userRepositoryObjectFactory = userRepositoryObjectFactory;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    @Value("${application.security.jwt.access-token.validity-mins}")
    public void setAccessTokenValidity(int accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    @Value("${application.security.jwt.refresh-token.validity-days}")
    public void setRefreshTokenValidity(int refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String getAccessToken(Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.MINUTE, accessTokenValidity);

        JWTCreator.Builder jwtBuilder = JWT.create().withSubject(accessTokenSubject).withIssuer(jwtIssuer).withAudience(audience);
        claims.forEach(jwtBuilder::withClaim);

        return jwtBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).sign(Algorithm.RSA256(rsaPublicKey, rsaPrivateKey));
    }

    public Boolean isAccessTokenValid(String accessToken, UserDetails userDetails) {
        Jwt decodedJwt = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build().decode(accessToken);
        String username = decodedJwt.getClaimAsString("username");
        String iss = decodedJwt.getClaimAsString("iss");
        String subject = decodedJwt.getSubject();

        return username.equals(userDetails.getUsername()) && iss.equals(jwtIssuer) && subject.equals(accessTokenSubject)
                && Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(Instant.now());
    }

    public String getRefreshToken(String username, Map<String, String> claims) {
        if (this.getUser(username) != null) {
            List<RefreshToken> refreshTokens = refreshTokenRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(token -> token.getForUser().getUsername().equals(username)).toList();

            if (refreshTokens.size() == 1) {
                RefreshToken refreshToken = refreshTokens.get(0);
                try {
                    Jwt jwt = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build().decode(refreshToken.getToken());

                    if (Objects.requireNonNull(jwt.getExpiresAt()).isAfter(Instant.now()) && jwt.getSubject().equals(refreshTokenSubject)
                            && jwt.getClaimAsString("iss").equals(jwtIssuer)) {
                        return refreshToken.getToken();
                    } else {
                        return this.createRefreshToken(username, UUID.randomUUID().toString(), claims);
                    }
                } catch (JwtValidationException e) {
                    log.warn("Malformed Refresh Token detected. Purging the refresh token and generating new token...");
                    refreshTokenRepositoryObjectFactory.getObject().delete(refreshToken);
                    return this.createRefreshToken(username, UUID.randomUUID().toString(), claims);
                }
            }

            return this.createRefreshToken(username, UUID.randomUUID().toString(), claims);
        }

        throw new UsernameNotFoundException("Incorrect username: " + username);
    }

    public Boolean isRefreshTokenValid(String refreshToken) {
        try {
            Jwt decodedJwt = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build().decode(refreshToken);

            List<RefreshToken> tokens = refreshTokenRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(t -> t.getForUser().getUsername().equals(decodedJwt.getClaimAsString("username"))).toList();

            if (tokens.size() == 1 && decodedJwt != null) {
                RefreshToken token = tokens.get(0);
                if (token.getToken().equals(decodedJwt.getTokenValue())) {
                    return Objects.requireNonNull(decodedJwt.getExpiresAt()).isAfter(Instant.now())
                            && decodedJwt.getSubject().equals(refreshTokenSubject)
                            && decodedJwt.getClaimAsString("iss").equals(jwtIssuer);
                }
            }
            return false;
        } catch (Exception e) {
            log.error((e.getCause() != null) ? e.getCause().getMessage() : e.getMessage());
            return false;
        }
    }

    private User getUser(String username) {
        if (userRepositoryObjectFactory.getObject().findById(username).isPresent()) {
            return userRepositoryObjectFactory.getObject().getById(username);
        } else {
            return null;
        }
    }

    private String createRefreshToken(String username, String jwtId, Map<String, String> claims) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());
        calendar.add(Calendar.DATE, refreshTokenValidity);

        JWTCreator.Builder refreshTokenBuilder = JWT.create().withSubject(refreshTokenSubject).withIssuer(jwtIssuer).withAudience(audience);
        claims.forEach(refreshTokenBuilder::withClaim);

        String refreshToken = refreshTokenBuilder.withNotBefore(new Date()).withIssuedAt(new Date())
                .withExpiresAt(calendar.getTime()).withJWTId(jwtId).sign(Algorithm.RSA256(rsaPublicKey, rsaPrivateKey));

        Thread thread = new Thread(() -> persistRefreshToken(username, jwtId, refreshToken));
        thread.start();

        return refreshToken;
    }

    private void persistRefreshToken(String username, String id, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(id);
        refreshToken.setToken(token);
        refreshToken.setForUser(this.getUser(username));

        refreshTokenRepositoryObjectFactory.getObject().saveAndFlush(refreshToken);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
}
