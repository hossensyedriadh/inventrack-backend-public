package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Product")
@Table(name = "products", schema = "inventrack")
public final class Product implements Serializable {
    @Serial
    private static final long serialVersionUID = 5765010773159717019L;

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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "updated_by", referencedColumnName = "username", insertable = false)
    private User updatedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter(AccessLevel.NONE)
    @Column(name = "updated_on", insertable = false)
    private LocalDateTime updatedOn;

    @OneToOne(targetEntity = PurchaseOrder.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "purchase_order_ref", referencedColumnName = "id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> images;

    @PrePersist
    private void initialize() {
        String[] uuid = UUID.randomUUID().toString().split("-");
        this.id = uuid[3].concat(uuid[4]);
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now(ZoneId.systemDefault());
    }
}
