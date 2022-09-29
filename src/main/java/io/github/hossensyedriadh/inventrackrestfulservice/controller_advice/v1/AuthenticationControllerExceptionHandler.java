package io.github.hossensyedriadh.inventrackrestfulservice.controller_advice.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.exception.GenericErrorResponse;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.InvalidCredentialsException;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.InvalidRefreshTokenException;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.UserAccountLockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = {"io.github.hossensyedriadh.inventrackrestfulservice.controller.authentication.v1"})
public class AuthenticationControllerExceptionHandler {
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public AuthenticationControllerExceptionHandler(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<GenericErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(httpServletRequest, HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(httpServletRequest, HttpStatus.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAccountLockedException.class)
    public ResponseEntity<GenericErrorResponse> handleUserAccountLockedException(UserAccountLockedException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(httpServletRequest, HttpStatus.FORBIDDEN, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<GenericErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(httpServletRequest, HttpStatus.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}
