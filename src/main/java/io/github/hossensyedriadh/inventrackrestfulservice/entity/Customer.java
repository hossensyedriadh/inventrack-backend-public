package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Customer")
@Table(name = "customers", schema = "inventrack")
public final class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = -403701503015247129L;

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

    @Email(message = "Invalid email address", regexp = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
    @Column(name = "email")
    private String email;

    @NotNull
    @Pattern(message = "Can contain only letters, numbers, colons(:), semicolons(;), commas(,), hyphens(-), forward slashes(/) and whitespaces ( )",
            regexp = "^[a-zA-Z0-9:;,\\-/\\s]+$")
    @Length(min = 5, message = "Must be at-least 5 characters long")
    @Column(name = "address", nullable = false)
    private String address;
}
