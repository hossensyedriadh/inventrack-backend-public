package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "products")
public final class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 4287209975587620092L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private String id;

    @NotNull(message = "Product name can not be null")
    @Pattern(message = "Invalid product name", regexp = "^[\\w\s.\\-()/\\\\]{5,}$")
    @Column(name = "name", updatable = false, nullable = false)
    private String productName;

    @NotNull(message = "Category can not be null")
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false, targetEntity = ProductCategory.class)
    @JoinColumn(name = "category", referencedColumnName = "name", nullable = false)
    private ProductCategory category;

    @Column(name = "specifications")
    private String specifications;

    @PositiveOrZero(message = "Stock must be greater than or equal to 0")
    @Min(value = 0, message = "Minimum stock is 0")
    @Column(name = "stock", nullable = false)
    private int stock;

    @Positive(message = "Price must be greater than 0")
    @Min(value = 1, message = "Minimum purchase price is 1")
    @Column(name = "price", nullable = false)
    private double price;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username", insertable = false)
    private User updatedBy;

    @Setter(AccessLevel.NONE)
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @OneToOne(targetEntity = PurchaseOrder.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "purchase_order_ref", referencedColumnName = "id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @PrePersist
    private void initialize() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[3].concat(uuid[4]);
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now();
    }
}
