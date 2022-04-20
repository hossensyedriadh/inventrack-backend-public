package io.github.hossensyedriadh.InvenTrackRESTfulService.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

@SuppressWarnings("unused")
public class UserAccountLockedException extends AuthenticationException {
    @Serial
    private static final long serialVersionUID = -3111324497150457211L;

    /**
     * Constructs an {@code AuthenticationException} with the specified message and root
     * cause.
     *
     * @param msg   the detail message
     * @param cause the root cause
     */
    public UserAccountLockedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public UserAccountLockedException(String msg) {
        super(msg);
    }
}
