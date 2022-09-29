package io.github.hossensyedriadh.inventrackrestfulservice.authentication.bearer_authentication.handler;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.GenericErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
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
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setHeader(HttpHeaders.DATE, String.valueOf(Date.from(Instant.now(Clock.system(
                ZoneId.systemDefault()
        )))));
        response.setLocale(Locale.ENGLISH);

        GenericErrorResponse errorResponse = new GenericErrorResponse(request, HttpStatus.FORBIDDEN,
                "You do not have permission to access this resource");

        JsonMapper jsonMapper = new JsonMapper();
        String json = jsonMapper.writeValueAsString(errorResponse);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json);
    }
}
