package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.entrypoint;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.GenericErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Serial
    private static final long serialVersionUID = 3254427823742667645L;

    /**
     * Commences an authentication scheme.
     * <p>
     * <code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code>
     * attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p>
     * Implementations should modify the headers on the <code>ServletResponse</code> as necessary to commence the
     * authentication process.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setLocale(Locale.ENGLISH);

        GenericErrorResponse errorResponse = new GenericErrorResponse(HttpStatus.UNAUTHORIZED,
                "Authentication is required to access this resource", request.getRequestURI());

        JsonMapper jsonMapper = new JsonMapper();
        String json = jsonMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
