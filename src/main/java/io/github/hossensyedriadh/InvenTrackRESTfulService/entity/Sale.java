package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.OrderStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.PaymentStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sales")
public final class Sale implements Serializable {
    @Serial
    private static final long serialVersionUID = 7972242389389368975L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    @PositiveOrZero(message = "Total payable amount must be greater than or equal to 0")
    @Column(name = "total_payable", nullable = false)
    private double totalPayable;

    @PositiveOrZero(message = "Total due amount must be greater than or equal to 0")
    @Column(name = "total_due", nullable = false)
    private double totalDue;

    @NotNull(message = "Customer can not be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false, targetEntity = Customer.class)
    @JoinColumn(name = "customer", referencedColumnName = "phone_no", nullable = false)
    private Customer customer;

    @NotNull(message = "Payment status can not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull(message = "Payment method can not be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false, targetEntity = PaymentMethod.class)
    @JoinColumn(name = "payment_method", referencedColumnName = "name", nullable = false)
    private PaymentMethod paymentMethod;

    @Pattern(regexp = "^[a-zA-Z0-9+:;*\\s.,\"'\\-()/\\\\]+$", message = "Invalid payment details")
    @Column(name = "payment_details")
    private String paymentDetails;

    @NotNull(message = "Order status can not be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @NotNull(message = "Delivery Medium can not be null")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false, targetEntity = DeliveryMedium.class)
    @JoinColumn(name = "delivery_medium", referencedColumnName = "name", nullable = false)
    private DeliveryMedium deliveryMedium;

    @Column(name = "notes", updatable = false)
    private String notes;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "added_by", referencedColumnName = "username", nullable = false)
    private User addedBy;

    @Setter(AccessLevel.NONE)
    @Column(name = "added_on", updatable = false, nullable = false)
    private LocalDateTime addedOn;

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
    }

    @PreUpdate
    private void update() {
        this.updatedOn = LocalDateTime.now();
    }
}
