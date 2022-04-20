package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Entity
@Table(name = "customers")
public final class Customer implements Serializable {
    @Serial
    private static final long serialVersionUID = -7153534601024309901L;

    @NotNull(message = "Customer name can not be null")
    @Pattern(message = "Invalid customer name", regexp = "^[\\w\s.]{4,}$")
    @Column(name = "name", updatable = false, nullable = false)
    private String name;

    @Id
    @NotNull(message = "Customer phone can not be null")
    @Pattern(message = "Invalid phone number", regexp = "^[0-9+]{8,20}$")
    @Column(name = "phone_no", unique = true, updatable = false, nullable = false)
    private String phoneNo;

    @Email(regexp = "\\S+@\\S+\\.\\S+",
            message = "Invalid email")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Customer address can not be null")
    @Pattern(regexp = "^[a-zA-Z0-9:;().,\\-\\s\\\\/]{5,}$", message = "Invalid address")
    @Column(name = "address", nullable = false)
    private String address;
}
