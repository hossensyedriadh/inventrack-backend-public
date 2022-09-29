package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hossensyedriadh.inventrackrestfulservice.configuration.datasource.PostgreSQLEnumType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "User")
@Table(name = "users", schema = "inventrack")
@TypeDef(name = "pgsql_user_authority_enum", typeClass = PostgreSQLEnumType.class)
public final class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 2475146822803075475L;

    @Id
    @NotNull
    @Pattern(message = "Invalid username", regexp = "^[a-zA-Z_]{4,75}$")
    @Column(name = "username", unique = true, updatable = false, nullable = false)
    private String username;

    @JsonIgnore
    @NotNull
    @Pattern(message = "Password must contain at-least 1 upper-case, 1 lower-case letter, 1 number and 1 special character",
            regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")
    @Length(message = "Password must be at-least 8 characters long", min = 8)
    @Column(name = "password", nullable = false)
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_user_authority_enum")
    @Column(name = "authority", nullable = false)
    private Authority authority;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "is_not_locked", nullable = false)
    private boolean accountNotLocked;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false, targetEntity = Profile.class)
    @JoinColumn(name = "profile_reference", referencedColumnName = "profile_id", nullable = false)
    private Profile profile;

    @PrePersist
    private void initialize() {
        this.enabled = true;
        this.accountNotLocked = true;
    }
}
