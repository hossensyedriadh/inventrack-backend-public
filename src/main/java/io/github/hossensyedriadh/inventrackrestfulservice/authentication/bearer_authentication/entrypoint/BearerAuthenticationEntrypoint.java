package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.entrypoint;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.GenericErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

@Component
public class BearerAuthenticationEntrypoint implements AuthenticationEntryPoint {
    /**
     * Commences an authentication scheme.
     * <p>
     * <code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code>
     * attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p>
     * Implementations should modify the headers on the <code>ServletResponse</code> as
     * necessary to commence the authentication process.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.DATE, String.valueOf(Date.from(Instant.now(Clock.system(
                ZoneId.systemDefault()
        )))));
        response.setLocale(Locale.ENGLISH);

        GenericErrorResponse errorResponse = new GenericErrorResponse(request, HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource");

        JsonMapper jsonMapper = new JsonMapper();
        String json = jsonMapper.writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json);
    }
}
