package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Supplier;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SupplierModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SupplierToSupplierModel implements Converter<Supplier, SupplierModel> {
    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public SupplierModel convert(Supplier source) {
        SupplierModel supplier = new SupplierModel();
        supplier.setName(source.getName());
        supplier.setPhone(source.getPhoneNo());
        supplier.setEmail((source.getEmail() != null) ? source.getEmail() : null);
        supplier.setAddress(source.getAddress());
        supplier.setWebsite((source.getWebsite() != null) ? source.getWebsite() : null);
        supplier.setNotes((source.getNotes() != null) ? source.getNotes() : null);
        supplier.setAddedOn(source.getAddedOn());
        supplier.setAddedBy(source.getAddedBy().getUsername());
        supplier.setUpdatedOn((source.getUpdatedOn() != null) ? source.getUpdatedOn() : null);
        supplier.setUpdatedBy((source.getUpdatedBy() != null) ? source.getUpdatedBy().getUsername() : null);

        return supplier;
    }
}
