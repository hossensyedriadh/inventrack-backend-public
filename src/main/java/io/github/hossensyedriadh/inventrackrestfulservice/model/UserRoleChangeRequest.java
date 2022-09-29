package io.github.hossensyedriadh.inventrackrestfulservice.model;

import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.InvitedUserAuthority;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class UserRoleChangeRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -6533317507152372526L;

    @NotNull
    @Pattern(message = "Invalid username", regexp = "^[a-zA-Z_]{4,75}$")
    private String username;

    @NotNull
    private InvitedUserAuthority role;
}
