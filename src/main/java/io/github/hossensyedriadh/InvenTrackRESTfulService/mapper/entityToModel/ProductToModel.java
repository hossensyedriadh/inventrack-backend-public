package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Product;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductToModel implements Converter<Product, ProductModel> {
    private final PurchaseOrderToModel toPurchaseOrderModel;

    @Autowired
    public ProductToModel(PurchaseOrderToModel toPurchaseOrderModel) {
        this.toPurchaseOrderModel = toPurchaseOrderModel;
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public ProductModel convert(Product source) {
        ProductModel model = new ProductModel();
        model.setId(source.getId());
        model.setName(source.getProductName());
        model.setCategory(source.getCategory().getName());
        model.setSpecifications((source.getSpecifications() != null) ? source.getSpecifications() : null);
        model.setStock(source.getStock());
        model.setPrice(source.getPrice());
        model.setUpdatedBy((source.getUpdatedBy() != null) ? source.getUpdatedBy().getUsername() : null);
        model.setUpdatedOn((source.getUpdatedOn() != null) ? source.getUpdatedOn() : null);
        model.setPurchaseOrder(this.toPurchaseOrderModel.convert(source.getPurchaseOrder()));

        return model;
    }
}
