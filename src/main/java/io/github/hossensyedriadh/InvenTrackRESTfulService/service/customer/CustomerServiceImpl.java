package io.github.hossensyedriadh.InvenTrackRESTfulService.service.customer;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Customer;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.CustomerToCustomerModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.CustomerModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.CustomerRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class CustomerServiceImpl implements CustomerService {
    private final ObjectFactory<CustomerRepository> customerRepositoryObjectFactory;
    private final CustomerToCustomerModel toModel;

    @Autowired
    public CustomerServiceImpl(ObjectFactory<CustomerRepository> customerRepositoryObjectFactory, CustomerToCustomerModel toModel) {
        this.customerRepositoryObjectFactory = customerRepositoryObjectFactory;
        this.toModel = toModel;
    }

    @Override
    public Page<CustomerModel> getCustomers(Pageable pageable) {
        Page<Customer> customerPage = customerRepositoryObjectFactory.getObject().findAll(pageable);

        return customerPage.map(toModel::convert);
    }

    @Override
    public Optional<CustomerModel> getCustomer(String phone) {
        if (customerRepositoryObjectFactory.getObject().findById(phone).isPresent()) {
            Customer customer = customerRepositoryObjectFactory.getObject().findById(phone).get();
            return Optional.ofNullable(toModel.convert(customer));
        }

        return Optional.empty();
    }

    @Override
    public Optional<CustomerModel> updateCustomer(CustomerModel customerModel) {
        if (customerRepositoryObjectFactory.getObject().findById(customerModel.getPhone()).isPresent()) {
            Customer customer = customerRepositoryObjectFactory.getObject().getById(customerModel.getPhone());
            customer.setEmail((customerModel.getEmail() != null) ? ((!customerModel.getEmail().equals(customer.getEmail())) ? customerModel.getEmail() : customer.getEmail())
                    : null);
            customer.setAddress((!customerModel.getAddress().equals(customer.getAddress())) ? customerModel.getAddress() : customer.getAddress());
            customerRepositoryObjectFactory.getObject().saveAndFlush(customer);

            Customer updatedCustomer = customerRepositoryObjectFactory.getObject().getById(customer.getPhoneNo());

            return Optional.ofNullable(this.toModel.convert(updatedCustomer));
        }

        return Optional.empty();
    }
}
