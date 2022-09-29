package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "SaleItem")
@Table(name = "sale_items", schema = "inventrack")
public final class SaleItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 8946579312998451413L;

    @JsonIgnore
    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", updatable = false, unique = true)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false, targetEntity = Product.class)
    @JoinColumn(name = "product", referencedColumnName = "id", nullable = false)
    private Product product;

    @Min(message = "Minimum quantity is 1", value = 1)
    @Column(name = "quantity", updatable = false, nullable = false)
    private int quantity;

    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    @Column(name = "price", updatable = false, nullable = false)
    private Double price;

    @JsonIgnore
    @NotNull
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false, targetEntity = Sale.class)
    @JoinColumn(name = "sale_ref", referencedColumnName = "id", nullable = false)
    private Sale sale;

    @PrePersist
    private void init() {
        this.id = UUID.randomUUID().toString();
    }
}
