package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.PurchaseOrder;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PurchaseOrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderToModel implements Converter<PurchaseOrder, PurchaseOrderModel> {
    private final SupplierToSupplierModel toSupplierModel;

    @Autowired
    public PurchaseOrderToModel(SupplierToSupplierModel toSupplierModel) {
        this.toSupplierModel = toSupplierModel;
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public PurchaseOrderModel convert(PurchaseOrder source) {
        PurchaseOrderModel model = new PurchaseOrderModel();
        model.setId(source.getId());
        model.setProductName(source.getProductName());
        model.setCategory(source.getCategory().getName());
        model.setSpecifications((source.getSpecifications() != null) ? source.getSpecifications() : null);
        model.setQuantity(source.getQuantity());
        model.setTotalPurchasePrice(source.getTotalPurchasePrice());
        model.setShippingCosts(source.getShippingCosts());
        model.setOtherCosts(source.getOtherCosts());
        model.setSellingPricePerUnit(source.getSellingPrice());
        model.setSupplier(this.toSupplierModel.convert(source.getSupplier()));
        model.setStatus(source.getStatus());
        model.setOrderType(source.getType());
        model.setProductId(source.getProductId());
        model.setAddedBy(source.getAddedBy().getUsername());
        model.setAddedOn(source.getAddedOn());
        model.setUpdatedBy((source.getUpdatedBy() != null) ? source.getUpdatedBy().getUsername() : null);
        model.setUpdatedOn((source.getUpdatedOn() != null) ? source.getUpdatedOn() : null);

        return model;
    }
}
