package io.github.hossensyedriadh.inventrackrestfulservice.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class SignupRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -2453034550321548568L;

    @NotNull
    @Pattern(message = "Invalid username", regexp = "^[a-zA-Z_]{4,75}$")
    private String username;

    @NotNull
    @Pattern(message = "Password must contain at-least 1 upper-case, 1 lower-case letter, 1 number and 1 special character",
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    private String password;

    @NotNull
    @Pattern(message = "Can contain only letters, whitespaces and periods", regexp = "^[A-Za-z\\s.]+$")
    @Length(message = "Length must be within 3-50 characters", min = 3, max = 50)
    private String firstName;

    @NotNull
    @Pattern(message = "Can contain only letters, whitespaces and periods", regexp = "^[A-Za-z\\s.]+$")
    @Length(message = "Length must be within 3-50 characters", min = 3, max = 50)
    private String lastName;

    @Pattern(message = "Must be a valid phone number with ISD code", regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$")
    private String phone;

    @NotNull
    private String invitationToken;
}
