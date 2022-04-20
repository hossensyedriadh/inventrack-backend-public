package io.github.hossensyedriadh.InvenTrackRESTfulService.authentication.bearer_auth_mechanism.filter;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ExpiredAccessTokenException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.GenericErrorResponse;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.InvalidAccessTokenException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.MalformedTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class ExceptionFilter extends OncePerRequestFilter {
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
        try {
            response.setLocale(Locale.ENGLISH);
            response.setContentType(request.getContentType());
            filterChain.doFilter(request, response);
        } catch (JwtException | InvalidAccessTokenException e) {
            this.setErrorResponse(HttpStatus.BAD_REQUEST, request, response, e);
        } catch (MalformedTokenException e) {
            this.setErrorResponse(HttpStatus.FORBIDDEN, request, response, e);
        } catch (ExpiredAccessTokenException e) {
            this.setErrorResponse(HttpStatus.UNAUTHORIZED, request, response, e);
        } catch (RuntimeException e) {
            this.setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request, response, e);
        }
    }

    public void setErrorResponse(HttpStatus httpStatus, HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        response.setStatus(httpStatus.value());
        response.setLocale(Locale.ENGLISH);

        GenericErrorResponse errorResponse = new GenericErrorResponse(httpStatus.value(), throwable, request.getRequestURI());

        try {
            JsonMapper jsonMapper = new JsonMapper();
            String json = jsonMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
