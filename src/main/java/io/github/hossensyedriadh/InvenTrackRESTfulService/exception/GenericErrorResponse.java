package io.github.hossensyedriadh.InvenTrackRESTfulService.exception;

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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SuppressWarnings("unused")
@Getter
@Setter
public class GenericErrorResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 4505777764544613882L;

    private int status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String error;
    private String path;

    private GenericErrorResponse() {
        this.timestamp = LocalDateTime.of(LocalDate.now(), LocalTime.now());
    }

    public GenericErrorResponse(int status) {
        this();
        this.status = status;
    }

    public GenericErrorResponse(HttpStatus status, String message, String path) {
        this();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }

    public GenericErrorResponse(int status, Throwable throwable, String path) {
        this();
        this.status = status;
        this.message = "Access denied";
        this.error = (throwable.getCause() != null) ? throwable.getCause().getMessage() : throwable.getMessage();
        this.path = path;
    }

    public GenericErrorResponse(int status, String message, String path) {
        this();
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public GenericErrorResponse(String message) {
        this();
        this.message = message;
    }

    public GenericErrorResponse(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public GenericErrorResponse(int status, String message, Throwable throwable, String path) {
        this();
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
