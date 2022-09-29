package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.hossensyedriadh.inventrackrestfulservice.configuration.datasource.PostgreSQLEnumType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.OrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "Sale")
@Table(name = "sales", schema = "inventrack")
@TypeDefs({
        @TypeDef(name = "pgsql_sale_payment_status_enum", typeClass = PostgreSQLEnumType.class),
        @TypeDef(name = "pgsql_sale_order_status_enum", typeClass = PostgreSQLEnumType.class)
})
public final class Sale implements Serializable {
    @Serial
    private static final long serialVersionUID = 3887442041270361216L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @Transient
    private List<SaleItem> products;

    @PositiveOrZero(message = "Total payable amount must be greater than or equal to 0")
    @Column(name = "total_payable", nullable = false)
    private double totalPayable;

    @PositiveOrZero(message = "Total due amount must be greater than or equal to 0")
    @Column(name = "total_due", nullable = false)
    private double totalDue;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false, targetEntity = Customer.class)
    @JoinColumn(name = "customer", referencedColumnName = "phone_no", nullable = false)
    private Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_sale_payment_status_enum")
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false, targetEntity = PaymentMethod.class)
    @JoinColumn(name = "payment_method", referencedColumnName = "name", nullable = false)
    private PaymentMethod paymentMethod;

    @Pattern(regexp = "^[a-zA-Z0-9+:;\\s.,\"'\\-()\\[\\]/]+$",
            message = "Can contain only letters, numbers, comma, period(.), +, :, ;, \", ', -, (), [], \\, / and whitespaces")
    @Column(name = "payment_details")
    private String paymentDetails;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_sale_order_status_enum")
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false, targetEntity = DeliveryMedium.class)
    @JoinColumn(name = "delivery_medium", referencedColumnName = "name", nullable = false)
    private DeliveryMedium deliveryMedium;

    @Column(name = "notes", updatable = false)
    private String notes;

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
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now(ZoneId.systemDefault());
    }
}
