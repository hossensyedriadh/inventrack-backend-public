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
import java.util.Objects;

@Getter
@Setter
public final class GenericErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 7590667292480740432L;

    private int status;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime timestamp;
    private String message;
    private String error;
    private String path;

    public GenericErrorResponse(int status) {
        this.status = status;
    }

    public GenericErrorResponse(HttpServletRequest httpServletRequest, HttpStatus status, String message) {
        this.timestamp = RequestContextUtils.getTimeZone(httpServletRequest) != null ?
                LocalDateTime.ofInstant(Instant.now(), ZoneId.of(Objects.requireNonNull(RequestContextUtils.getTimeZone(httpServletRequest)).toZoneId().getId()))
                : LocalDateTime.now(ZoneId.systemDefault());
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = httpServletRequest.getRequestURI();
    }

    public GenericErrorResponse(HttpServletRequest request, HttpStatus status, Throwable throwable) {
        this.timestamp = RequestContextUtils.getTimeZone(request) != null ?
                LocalDateTime.ofInstant(Instant.now(), ZoneId.of(Objects.requireNonNull(RequestContextUtils.getTimeZone(request)).toZoneId().getId()))
                : LocalDateTime.now(ZoneId.systemDefault());
        this.status = status.value();
        this.message = status.getReasonPhrase();
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = request.getRequestURI();
    }

    public GenericErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public GenericErrorResponse(String message) {
        this.message = message;
    }

    public GenericErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public GenericErrorResponse(int status, String message, Throwable throwable, String path) {
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
