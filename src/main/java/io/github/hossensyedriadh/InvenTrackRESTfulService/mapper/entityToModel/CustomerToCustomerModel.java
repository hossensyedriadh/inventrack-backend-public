package io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Customer;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.CustomerModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerToCustomerModel implements Converter<Customer, CustomerModel> {
    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public CustomerModel convert(Customer source) {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setName(source.getName());
        customerModel.setPhone(source.getPhoneNo());
        customerModel.setEmail((source.getEmail() != null) ? source.getEmail() : null);
        customerModel.setAddress(source.getAddress());

        return customerModel;
    }
}
