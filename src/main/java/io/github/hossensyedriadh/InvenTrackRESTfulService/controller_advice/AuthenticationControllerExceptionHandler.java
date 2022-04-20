package io.github.hossensyedriadh.InvenTrackRESTfulService.controller_advice;

import io.github.hossensyedriadh.InvenTrackRESTfulService.controller.authentication.v1.AuthenticationController;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.GenericErrorResponse;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.InvalidCredentialsException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.InvalidRefreshTokenException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.UserAccountLockedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackageClasses = AuthenticationController.class)
public class AuthenticationControllerExceptionHandler {
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public AuthenticationControllerExceptionHandler(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<GenericErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), e.getPath());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), httpServletRequest.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAccountLockedException.class)
    public ResponseEntity<GenericErrorResponse> handleAccountLockedException(UserAccountLockedException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), httpServletRequest.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<GenericErrorResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e.getPath());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
