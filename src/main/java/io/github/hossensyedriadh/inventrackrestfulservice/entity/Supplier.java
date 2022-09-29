package io.github.hossensyedriadh.inventrackrestfulservice.entity;

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
import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Supplier")
@Table(name = "suppliers", schema = "inventrack")
public final class Supplier implements Serializable {
    @Serial
    private static final long serialVersionUID = 827504301669231697L;

    @NotNull
    @Pattern(message = "Can contain only letters, whitespaces and periods", regexp = "^[a-zA-Z\\s.]+$")
    @Length(min = 4, message = "Name must be at-least 4 characters")
    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Id
    @NotNull
    @Pattern(message = "Must be a valid phone number with ISD code", regexp = "^\\+(?:[0-9] ?){6,14}[0-9]$")
    @Column(name = "phone_no", unique = true, updatable = false, nullable = false)
    private String phoneNo;

    @Email(message = "Invalid email", regexp = "\\S+@\\S+\\.\\S+")
    @Column(name = "email")
    private String email;

    @NotNull
    @Pattern(message = "Can contain only letters, numbers, colons(:), semicolons(;), commas(,), hyphens(-), forward slashes(/) and whitespaces ( )",
            regexp = "^[a-zA-Z0-9:;,\\-/\\s]$")
    @Length(min = 5, message = "Must be at-least 5 characters long")
    @Column(name = "address", nullable = false)
    private String address;

    @URL(protocol = "https", regexp = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
            message = "Invalid URL")
    @Column(name = "website")
    private String website;

    @Column(name = "notes")
    private String notes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "added_by", referencedColumnName = "username", nullable = false)
    private User addedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Column(name = "added_on", updatable = false, nullable = false)
    private LocalDateTime addedOn;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username", insertable = false)
    private User updatedBy;

    @Setter(AccessLevel.NONE)
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @PrePersist
    private void initialize() {
        this.addedOn = LocalDateTime.now(ZoneId.systemDefault());
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now(ZoneId.systemDefault());
    }
}
