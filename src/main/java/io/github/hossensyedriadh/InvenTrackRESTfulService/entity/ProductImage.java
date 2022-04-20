package io.github.hossensyedriadh.InvenTrackRESTfulService.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_images")
public final class ProductImage implements Serializable {
    @Serial
    private static final long serialVersionUID = 2508306949474627564L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "tag", unique = true, updatable = false, nullable = false)
    private String tag;

    @NotNull(message = "Product image url can not be null")
    @URL(message = "Protocol must be https", protocol = "https")
    @Column(name = "static_cdn_url", nullable = false)
    private String url;

    @ManyToOne(targetEntity = Product.class, fetch = FetchType.EAGER, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "for_product", referencedColumnName = "id", nullable = false)
    private Product forProduct;

    @PrePersist
    private void init() {
        this.tag = UUID.randomUUID().toString();
    }
}
