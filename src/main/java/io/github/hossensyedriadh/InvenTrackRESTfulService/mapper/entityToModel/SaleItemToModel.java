package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SaleItem;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SaleItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SaleItemToModel implements Converter<SaleItem, SaleItemModel> {
    private final ProductToModel toProductModel;

    @Autowired
    public SaleItemToModel(ProductToModel toProductModel) {
        this.toProductModel = toProductModel;
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public SaleItemModel convert(SaleItem source) {
        SaleItemModel saleItemModel = new SaleItemModel();
        saleItemModel.setProduct(toProductModel.convert(source.getProduct()));
        saleItemModel.setQuantity(source.getQuantity());
        saleItemModel.setPrice(source.getPrice());

        return saleItemModel;
    }
}
