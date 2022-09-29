package io.github.hossensyedriadh.inventrackrestfulservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public final class ValidationErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 6073678555180321958L;

    private int status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime timestamp;

    private String message;

    private String error;

    private String path;

    private List<String> details;

    public ValidationErrorResponse(int status) {
        this.status = status;
    }

    public ValidationErrorResponse(HttpServletRequest httpServletRequest, HttpStatus status, String message) {
        this.timestamp = RequestContextUtils.getTimeZone(httpServletRequest) != null ?
                LocalDateTime.ofInstant(Instant.now(), ZoneId.of(Objects.requireNonNull(RequestContextUtils.getTimeZone(httpServletRequest)).toZoneId().getId()))
                :  LocalDateTime.now(ZoneId.systemDefault());
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = httpServletRequest.getRequestURI();
    }

    public ValidationErrorResponse(HttpStatus status, String message, List<String> details, String path) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.details = details;
        this.path = path;
    }

    public ValidationErrorResponse(HttpServletRequest httpServletRequest, HttpStatus status, String message, List<String> details) {
        this.timestamp = RequestContextUtils.getTimeZone(httpServletRequest) != null ?
                LocalDateTime.ofInstant(Instant.now(), ZoneId.of(Objects.requireNonNull(RequestContextUtils.getTimeZone(httpServletRequest)).toZoneId().getId()))
                :  LocalDateTime.now(ZoneId.systemDefault());
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.details = details;
        this.path = httpServletRequest.getRequestURI();
    }

    public ValidationErrorResponse(int status, Throwable throwable, String path) {
        this.status = status;
        this.message = "Validation failed";
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = path;
    }

    public ValidationErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public ValidationErrorResponse(String message) {
        this.message = message;
    }

    public ValidationErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ValidationErrorResponse(int status, String message, Throwable throwable, String path) {
        this.status = status;
        this.message = message;
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = path;
    }

    public String convertToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper.writeValueAsString(this);
    }
}
