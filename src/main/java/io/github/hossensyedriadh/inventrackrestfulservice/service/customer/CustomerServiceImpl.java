package io.github.hossensyedriadh.inventrackrestfulservice.service.customer;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Customer;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                               HttpServletRequest httpServletRequest) {
        this.customerRepository = customerRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<Customer> customers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Customer customer(String phone) {
        if (customerRepository.findById(phone).isPresent()) {
            return customerRepository.findById(phone).get();
        }

        throw new ResourceException("Customer not found with phone: " + phone, HttpStatus.BAD_REQUEST,
                httpServletRequest);
    }

    @Override
    public Customer updateCustomer(Customer updatedCustomer) {
        if (customerRepository.findById(updatedCustomer.getPhoneNo()).isPresent()) {
            return customerRepository.save(updatedCustomer);
        }

        throw new ResourceException("No customer found with phone: " + updatedCustomer.getPhoneNo(), HttpStatus.BAD_REQUEST,
                httpServletRequest);
    }
}
