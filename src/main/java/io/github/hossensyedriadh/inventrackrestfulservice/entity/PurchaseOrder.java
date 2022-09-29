package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hossensyedriadh.inventrackrestfulservice.configuration.datasource.PostgreSQLEnumType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PurchaseOrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PurchaseOrderType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "PurchaseOrder")
@Table(name = "purchase_orders", schema = "inventrack")
@TypeDefs({
        @TypeDef(name = "pgsql_purchase_order_status_enum", typeClass = PostgreSQLEnumType.class),
        @TypeDef(name = "pgsql_purchase_order_type_enum", typeClass = PostgreSQLEnumType.class)
})
public final class PurchaseOrder implements Serializable {
    @Serial
    private static final long serialVersionUID = 9136088204907264708L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private String id;

    @NotNull
    @Pattern(message = "Can contain only letters, numbers, hyphens(-), brackets((), []) and whitespaces", regexp = "^[a-zA-Z0-9-()\\[\\]\\s]+$")
    @Length(min = 5, max = 120, message = "Length must be within 5-120 characters")
    @Column(name = "name", updatable = false, nullable = false)
    private String productName;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH},
            optional = false, targetEntity = ProductCategory.class)
    @JoinColumn(name = "category", referencedColumnName = "name", nullable = false)
    private ProductCategory category;

    @Column(name = "specifications")
    private String specifications;

    @NotNull
    @Positive(message = "Quantity must be greater than 0")
    @Min(value = 1, message = "Minimum quantity is 1")
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotNull
    @Positive(message = "Total purchase price must be greater than 0")
    @Min(value = 1, message = "Minimum purchase price is 1")
    @Column(name = "total_purchase_price", nullable = false)
    private double totalPurchasePrice;

    @NotNull
    @PositiveOrZero(message = "Shipping costs must be greater than or equal to 0")
    @Min(value = 0, message = "Shipping cost can't be lower than 0")
    @Column(name = "shipping_costs", nullable = false)
    private double shippingCosts;

    @NotNull
    @PositiveOrZero(message = "Other costs must be greater than or equal to 0")
    @Min(value = 0, message = "Cost can't be lower than 0")
    @Column(name = "other_costs", nullable = false)
    private double otherCosts;

    @NotNull
    @Positive(message = "Selling price must be greater than 0")
    @Min(value = 1, message = "Minimum selling price is 1")
    @Column(name = "selling_price", nullable = false)
    private double sellingPricePerUnit;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false, targetEntity = Supplier.class)
    @JoinColumn(name = "supplier_reference", referencedColumnName = "phone_no", nullable = false)
    private Supplier supplier;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_purchase_order_status_enum")
    @Column(name = "status", nullable = false)
    private PurchaseOrderStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_purchase_order_type_enum")
    @Column(name = "type", nullable = false, updatable = false)
    private PurchaseOrderType orderType;

    @Column(name = "product_id", updatable = false)
    private String productId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "added_by", referencedColumnName = "username", nullable = false)
    private User addedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Column(name = "added_on", updatable = false, nullable = false)
    private LocalDateTime addedOn;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username", insertable = false)
    private User updatedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @PrePersist
    private void initialize() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[2].concat(uuid[3]).concat(uuid[4]);
        this.addedOn = LocalDateTime.now(ZoneId.systemDefault());

        if (this.orderType == PurchaseOrderType.NEW_PRODUCT) {
            this.productId = null;
        }
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now(ZoneId.systemDefault());
    }
}
