package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Profile")
@Table(name = "profiles", schema = "inventrack")
public final class Profile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1378148523005328647L;

    @JsonIgnore
    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "profile_id", unique = true, updatable = false)
    private String id;

    @NotNull
    @Pattern(message = "Can contain only letters, whitespaces and periods", regexp = "^[A-Za-z\\s.]+$")
    @Length(message = "Length must be within 3-50 characters", min = 3, max = 50)
    @Column(name = "first_name", updatable = false, nullable = false)
    private String firstName;

    @NotNull
    @Pattern(message = "Can contain only letters, whitespaces and periods", regexp = "^[A-Za-z\\s.]+$")
    @Length(message = "Length must be within 3-50 characters", min = 3, max = 50)
    @Column(name = "last_name", updatable = false, nullable = false)
    private String lastName;

    @NotNull
    @Email(message = "Invalid email address", regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Pattern(message = "Must be a valid phone number with ISD code", regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$")
    @Column(name = "phone_no")
    private String phone;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Column(name = "user_since", updatable = false, nullable = false)
    private Date userSince;

    @URL(protocol = "https", regexp = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
            message = "Invalid URL")
    @Column(name = "avatar")
    private String avatar;

    @PrePersist
    private void initialize() {
        this.id = UUID.randomUUID().toString();
        this.userSince = Date.valueOf(LocalDate.now(ZoneId.systemDefault()));
    }
}
