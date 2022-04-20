package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_categories")
public final class ProductCategory implements Serializable {
    @Serial
    private static final long serialVersionUID = -8655274146382286065L;

    @NotNull(message = "Product Category name can not be null")
    @Id
    @Pattern(message = "Invalid product category", regexp = "^[a-zA-Z\\s-]{4,150}$")
    @Column(name = "name", unique = true, updatable = false, nullable = false)
    private String name;
}
