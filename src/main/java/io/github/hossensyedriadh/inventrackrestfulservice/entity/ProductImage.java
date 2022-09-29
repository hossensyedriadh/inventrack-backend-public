package io.github.hossensyedriadh.inventrackrestfulservice.entity;

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
@Entity(name = "ProductImage")
@Table(name = "product_images", schema = "inventrack")
public final class ProductImage implements Serializable {
    @Serial
    private static final long serialVersionUID = -632634940582702405L;
    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = "tag", unique = true, updatable = false, nullable = false)
    private String tag;

    @NotNull
    @URL(message = "Invalid URL", protocol = "https",
            regexp = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})")
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
