package io.github.hossensyedriadh.inventrackrestfulservice.entity;

import io.github.hossensyedriadh.inventrackrestfulservice.configuration.datasource.PostgreSQLEnumType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.FinanceRecordType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "FinanceRecord")
@Table(name = "finance_records", schema = "inventrack")
@TypeDef(name = "pgsql_finance_record_type_enum", typeClass = PostgreSQLEnumType.class)
public final class FinanceRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = -4334213309462670271L;

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false)
    private Integer id;

    @Column(name = "month", updatable = false, nullable = false)
    private Integer month;

    @Column(name = "year", updatable = false, nullable = false)
    private Integer year;

    @Column(name = "value", nullable = false)
    private Double value;

    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_finance_record_type_enum")
    @Column(name = "record_type", updatable = false, nullable = false)
    private FinanceRecordType type;

    @OneToOne(targetEntity = PurchaseOrder.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "purchase_order_ref", referencedColumnName = "id")
    private PurchaseOrder purchaseOrder;

    @OneToOne(targetEntity = Sale.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "sale_order_ref", referencedColumnName = "id")
    private Sale sale;
}
