package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profiles")
public final class Profile implements Serializable {
    @Serial
    private static final long serialVersionUID = 365972065598168374L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "profile_id", unique = true, updatable = false)
    private String id;

    @NotNull(message = "First name can not be null")
    @Pattern(message = "Invalid first name", regexp = "^[A-Za-z\\s.]+$")
    @Column(name = "first_name", updatable = false, nullable = false)
    private String firstName;

    @NotNull(message = "Last name can not be null")
    @Pattern(message = "Invalid last name", regexp = "^[A-Za-z\\s.]+$")
    @Column(name = "last_name", updatable = false, nullable = false)
    private String lastName;

    @NotNull(message = "Email can not be null")
    @Email(message = "Invalid email", regexp = "\\S+@\\S+\\.\\S+")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Pattern(message = "Invalid phone number", regexp = "^[0-9+]{11,15}$")
    @Column(name = "phone_no")
    private String phoneNo;

    @Setter(AccessLevel.NONE)
    @Column(name = "user_since", updatable = false, nullable = false)
    private Date userSince;

    @URL(regexp = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
            message = "Invalid avatar url")
    @Column(name = "avatar")
    private String avatar;

    @PrePersist
    private void initialize() {
        this.id = UUID.randomUUID().toString();
        this.userSince = Date.valueOf(LocalDate.now());
    }
}
