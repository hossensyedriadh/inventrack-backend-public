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
public final class PasswordChangeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8938069924095522850L;

    private String currentPassword;

    @NotNull
    @Pattern(message = "Password must contain at-least 1 upper-case, 1 lower-case letter, 1 number and 1 special character",
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    @Length(message = "Password must be at-least 8 characters long", min = 8)
    private String newPassword;
}
