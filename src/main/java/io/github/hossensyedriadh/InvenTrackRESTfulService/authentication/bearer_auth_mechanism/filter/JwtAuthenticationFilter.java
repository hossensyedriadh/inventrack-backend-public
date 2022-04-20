package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.service.JwtService;
import io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.service.JwtUserDetailsService;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ExpiredAccessTokenException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.InvalidAccessTokenException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.MalformedTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUserDetailsService jwtUserDetailsService;
    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(JwtUserDetailsService jwtUserDetailsService, JwtService jwtService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtService = jwtService;
    }

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
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String username;
            String accessToken;

            if (requestTokenHeader != null) {
                if (requestTokenHeader.startsWith("Bearer ")) {
                    accessToken = requestTokenHeader.substring(7);

                    try {
                        username = jwtService.jwtDecoder().decode(accessToken).getClaimAsString("username");
                    } catch (IllegalArgumentException e) {
                        throw new JwtException("Unable to parse Access Token", e);
                    } catch (TokenExpiredException e) {
                        throw new ExpiredAccessTokenException("Access Token expired", e, request.getRequestURI());
                    }
                } else {
                    throw new MalformedTokenException("Access token should start with the provided token type", request.getRequestURI());
                }
            } else {
                throw new InvalidAccessTokenException("Missing access token", request.getRequestURI());
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && (authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
                UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

                if (jwtService.isAccessTokenValid(accessToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    throw new InvalidAccessTokenException("Invalid Access token", request.getRequestURI());
                }
            }
        }

        response.setLocale(Locale.ENGLISH);
        filterChain.doFilter(request, response);
    }
}
