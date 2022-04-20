package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public final class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 5333142379795806876L;

    @NotNull(message = "Username can not be null")
    @Id
    @Pattern(message = "Invalid username", regexp = "^[a-zA-Z_]{4,75}$")
    @Column(name = "username", unique = true, updatable = false, nullable = false)
    private String username;

    @NotNull(message = "Password can not be null")
    @Pattern(message = "Invalid password", regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @NotNull(message = "Authority can not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private Authority authority;

    @Column(name = "is_not_locked", nullable = false)
    private boolean accountNotLocked;

    @NotNull(message = "Profile can not be null")
    @OneToOne(cascade = CascadeType.ALL, optional = false, targetEntity = Profile.class)
    @JoinColumn(name = "profile_reference", referencedColumnName = "profile_id", nullable = false)
    private Profile profile;

    @PrePersist
    private void initialize() {
        this.enabled = true;
        this.accountNotLocked = true;
    }
}
