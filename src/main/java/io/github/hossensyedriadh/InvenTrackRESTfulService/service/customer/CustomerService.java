package io.github.hossensyedriadh.InvenTrackRESTfulService.service.customer;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.CustomerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public sealed interface CustomerService permits CustomerServiceImpl {
    Page<CustomerModel> getCustomers(Pageable pageable);

    Optional<CustomerModel> getCustomer(String phone);

    Optional<CustomerModel> updateCustomer(CustomerModel customerModel);
}
