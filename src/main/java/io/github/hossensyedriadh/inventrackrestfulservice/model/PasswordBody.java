package io.github.hossensyedriadh.inventrackrestfulservice.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public final class PasswordBody implements Serializable {
    @Serial
    private static final long serialVersionUID = 2568121686953927291L;

    private String password;
}
