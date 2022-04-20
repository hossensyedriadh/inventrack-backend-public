package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sale_items")
public final class SaleItem implements Serializable {
    @Serial
    private static final long serialVersionUID = -6564801702986687504L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", updatable = false, unique = true)
    private String id;

    @NotNull(message = "Product can not be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false, targetEntity = Product.class)
    @JoinColumn(name = "product", referencedColumnName = "id", nullable = false)
    private Product product;

    @Min(message = "Minimum quantity is 1", value = 1)
    @Column(name = "quantity", updatable = false, nullable = false)
    private int quantity;

    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    @Column(name = "price", updatable = false, nullable = false)
    private Double price;

    @NotNull(message = "Sale can not be null")
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false, targetEntity = Sale.class)
    @JoinColumn(name = "sale_ref", referencedColumnName = "id", nullable = false)
    private Sale sale;

    @PrePersist
    private void init() {
        this.id = UUID.randomUUID().toString();
    }
}
