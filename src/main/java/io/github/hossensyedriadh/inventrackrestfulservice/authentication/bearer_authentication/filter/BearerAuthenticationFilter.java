package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service.BearerAuthenticationService;
import io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.service.BearerAuthenticationUserDetailsService;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ExpiredAccessTokenException;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.InvalidAccessTokenException;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.MalformedTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

@Component
public class BearerAuthenticationFilter extends OncePerRequestFilter {
    private final BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService;
    private final BearerAuthenticationService bearerAuthenticationService;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public BearerAuthenticationFilter(BearerAuthenticationUserDetailsService bearerAuthenticationUserDetailsService,
                                      BearerAuthenticationService bearerAuthenticationService,
                                      JwtDecoder jwtDecoder) {
        this.bearerAuthenticationUserDetailsService = bearerAuthenticationUserDetailsService;
        this.bearerAuthenticationService = bearerAuthenticationService;
        this.jwtDecoder = jwtDecoder;
    }

    @Value("${bearer-authentication.token.access-token.type}")
    private String accessTokenType;

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request     request received
     * @param response    response served
     * @param filterChain chain of filters
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String username;
            String accessToken;

            if (authorizationHeader != null) {
                if (authorizationHeader.startsWith(this.accessTokenType + " ")) {
                    accessToken = authorizationHeader.substring(this.accessTokenType.length() + 1);

                    try {
                        username = jwtDecoder.decode(accessToken).getClaimAsString("username");
                    } catch (IllegalArgumentException e) {
                        throw new JwtException("Unable to parse access token", e);
                    } catch (TokenExpiredException e) {
                        throw new ExpiredAccessTokenException("Access token expired", e, request);
                    }
                } else {
                    throw new MalformedTokenException("Access token should be prepended with " +
                            "access token type, i.e.: 'Bearer ' (without quotes)", request);
                }
            } else {
                throw new InvalidAccessTokenException("Missing / Invalid access token", request);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && (authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
                UserDetails userDetails = this.bearerAuthenticationUserDetailsService.loadUserByUsername(username);

                if (bearerAuthenticationService.isAccessTokenValid(accessToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new InvalidAccessTokenException("Invalid access token", request);
                }
            }
        }

        response.setLocale(Locale.ENGLISH);
        response.setHeader(HttpHeaders.DATE, String.valueOf(Date.from(Instant.now(Clock.system(
                ZoneId.systemDefault()
        )))));
        filterChain.doFilter(request, response);
    }
}
