package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "PaymentMethod")
@Table(name = "payment_methods", schema = "inventrack")
public final class PaymentMethod implements Serializable {
    @Serial
    private static final long serialVersionUID = -5227359102992311608L;

    @Id
    @NotNull
    @Pattern(message = "Can contain only letters, hyphens and whitespaces", regexp = "^[a-zA-Z\\s-]+$")
    @Length(min = 3, max = 50, message = "Must be within 3-50 characters length")
    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;
}
