package io.github.hossensyedriadh.InvenTrackRESTfulService.exception;

import org.springframework.http.HttpStatus;

import java.io.Serial;

@SuppressWarnings("unused")
public class ResourceCrudException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7270990626078345910L;

    private HttpStatus httpStatus;
    private String path;

    /**
     * Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    public ResourceCrudException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.  <p>Note that the detail message
     * associated with {@code cause} is <i>not</i> automatically incorporated in this runtime exception's detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A {@code null}
     *                value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.4
     */
    public ResourceCrudException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     * @param httpStatus HttpStatus to be forwarded
     * @param path    path of the requested resource.
     */
    public ResourceCrudException(String message, HttpStatus httpStatus, String path) {
        super(message);
        this.httpStatus = httpStatus;
        this.path = path;
    }

    /**
     * Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     * @param httpStatus HttpStatus to be forwarded
     */
    public ResourceCrudException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getPath() {
        return path;
    }
}
