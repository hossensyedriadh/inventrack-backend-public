package io.github.hossensyedriadh.inventrackrestfulservice.service.customer;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Page<Customer> customers(Pageable pageable);

    Customer customer(String phone);

    Customer updateCustomer(Customer updatedCustomer);
}
