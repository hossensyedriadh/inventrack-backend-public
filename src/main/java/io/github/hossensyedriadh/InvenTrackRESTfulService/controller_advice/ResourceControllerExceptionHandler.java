package io.github.hossensyedriadh.InvenTrackRESTfulService.controller_advice;

import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.GenericErrorResponse;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource")
public class ResourceControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceCrudException.class)
    public ResponseEntity<?> handleResourceCrudException(ResourceCrudException resourceCrudException) {
        GenericErrorResponse errorResponse = new GenericErrorResponse(resourceCrudException.getHttpStatus(),
                (resourceCrudException.getCause() != null) ? resourceCrudException.getCause().getMessage() : resourceCrudException.getMessage(),
                resourceCrudException.getPath());

        return new ResponseEntity<>(errorResponse, resourceCrudException.getHttpStatus());
    }
}
