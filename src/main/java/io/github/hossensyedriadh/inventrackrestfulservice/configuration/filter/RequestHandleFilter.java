package io.github.hossensyedriadh.inventrackrestfulservice.configuration.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
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
import java.util.UUID;

@Component
public class RequestHandleFilter extends OncePerRequestFilter {
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
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        response.setLocale(Locale.ENGLISH);
        response.setHeader(HttpHeaders.DATE, String.valueOf(Date.from(Instant.now(Clock.system(
                ZoneId.systemDefault()
        )))));
        request.setAttribute("Request-ID", UUID.randomUUID().toString());

        filterChain.doFilter(request, response);
    }
}
