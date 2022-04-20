package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.handler;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.GenericErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException in the event of an IOException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setLocale(Locale.ENGLISH);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        GenericErrorResponse errorResponse = new GenericErrorResponse(HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource", request.getRequestURI());

        JsonMapper jsonMapper = new JsonMapper();
        String json = jsonMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
