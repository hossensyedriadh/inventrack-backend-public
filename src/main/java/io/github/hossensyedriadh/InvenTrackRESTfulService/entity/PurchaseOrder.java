package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.ProductPurchaseStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.PurchaseOrderType;
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
@Table(name = "purchase_orders")
public final class PurchaseOrder implements Serializable {
    @Serial
    private static final long serialVersionUID = -834368773878328273L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private String id;

    @NotNull(message = "Product name can not be null")
    @Pattern(message = "Invalid product name", regexp = "^[\\w\s.\\-()/\\\\]{5,}$")
    @Column(name = "name", updatable = false, nullable = false)
    private String productName;

    @NotNull(message = "Product category can not be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH},
            optional = false, targetEntity = ProductCategory.class)
    @JoinColumn(name = "category", referencedColumnName = "name", nullable = false)
    private ProductCategory category;

    @Column(name = "specifications")
    private String specifications;

    @Positive(message = "Quantity must be greater than 0")
    @Min(value = 1, message = "Minimum quantity is 1")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Positive(message = "Total purchase price must be greater than 0")
    @Min(value = 1, message = "Minimum purchase price is 1")
    @Column(name = "total_purchase_price", nullable = false)
    private double totalPurchasePrice;

    @PositiveOrZero(message = "Shipping costs must be greater than or equal to 0")
    @Min(value = 0, message = "Shipping cost can't be lower than 0")
    @Column(name = "shipping_costs", nullable = false)
    private double shippingCosts;

    @PositiveOrZero(message = "Other costs must be greater than or equal to 0")
    @Min(value = 0, message = "Cost can't be lower than 0")
    @Column(name = "other_costs", nullable = false)
    private double otherCosts;

    @Positive(message = "Selling price must be greater than 0")
    @Min(value = 1, message = "Minimum selling price is 1")
    @Column(name = "selling_price", nullable = false)
    private double sellingPrice;

    @Setter(AccessLevel.NONE)
    @Column(name = "added_on", updatable = false, nullable = false)
    private LocalDateTime addedOn;

    @NotNull(message = "Supplier can not be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false, targetEntity = Supplier.class)
    @JoinColumn(name = "supplier_reference", referencedColumnName = "phone_no", nullable = false)
    private Supplier supplier;

    @NotNull(message = "Purchase status can not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductPurchaseStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private PurchaseOrderType type;

    @Column(name = "product_id", updatable = false)
    private String productId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "added_by", referencedColumnName = "username", nullable = false)
    private User addedBy;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username", insertable = false)
    private User updatedBy;

    @Setter(AccessLevel.NONE)
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @PrePersist
    private void initialize() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[2].concat(uuid[3]).concat(uuid[4]);
        this.addedOn = LocalDateTime.now();
        if (this.type == PurchaseOrderType.NEW_PRODUCT) {
            this.productId = null;
        }
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now();
    }
}
