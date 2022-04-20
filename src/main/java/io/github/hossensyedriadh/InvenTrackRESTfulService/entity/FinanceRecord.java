package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.FinanceRecordType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "finance_records")
public final class FinanceRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = 6339886184115017129L;

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private Integer id;

    @Column(name = "month", updatable = false, nullable = false)
    private int month;

    @Column(name = "year", updatable = false, nullable = false)
    private int year;

    @Column(name = "value", nullable = false)
    private double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "record_type", updatable = false, nullable = false)
    private FinanceRecordType type;

    @OneToOne(targetEntity = PurchaseOrder.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "purchase_order_ref", referencedColumnName = "id")
    private PurchaseOrder purchaseOrder;

    @OneToOne(targetEntity = Sale.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "sale_order_ref", referencedColumnName = "id")
    private Sale sale;
}
