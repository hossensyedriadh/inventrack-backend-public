package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Sale;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SaleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SaleToModel implements Converter<Sale, SaleModel> {
    private final CustomerToCustomerModel toCustomerModel;

    @Autowired
    public SaleToModel(CustomerToCustomerModel toCustomerModel) {
        this.toCustomerModel = toCustomerModel;
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public SaleModel convert(Sale source) {
        SaleModel saleModel = new SaleModel();
        saleModel.setId(source.getId());
        saleModel.setTotalPayable(source.getTotalPayable());
        saleModel.setTotalDue(source.getTotalDue());
        saleModel.setCustomer(this.toCustomerModel.convert(source.getCustomer()));
        saleModel.setPaymentStatus(source.getPaymentStatus());
        saleModel.setPaymentMethod(source.getPaymentMethod().getName());
        saleModel.setPaymentDetails((source.getPaymentDetails() != null) ? source.getPaymentDetails() : null);
        saleModel.setOrderStatus(source.getOrderStatus());
        saleModel.setDeliveryMedium(source.getDeliveryMedium().getName());
        saleModel.setNotes((source.getNotes() != null) ? source.getNotes() : null);
        saleModel.setAddedBy(source.getAddedBy().getUsername());
        saleModel.setAddedOn(source.getAddedOn());
        saleModel.setUpdatedBy((source.getUpdatedBy() != null) ? source.getAddedBy().getUsername() : null);
        saleModel.setUpdatedOn((source.getUpdatedOn() != null) ? source.getUpdatedOn() : null);

        return saleModel;
    }
}
