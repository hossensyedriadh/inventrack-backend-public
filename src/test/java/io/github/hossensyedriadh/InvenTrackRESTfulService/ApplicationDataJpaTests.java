package io.github.hossensyedriadh.InvenTrackRESTfulService;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Customer;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Supplier;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.CustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicationDataJpaTests {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void when_persisting_should_return_customer() {
        Customer customer = new Customer();
        customer.setName("Spring Boot");
        customer.setPhoneNo("+123456789");
        customer.setEmail("test@springboot.com");
        customer.setAddress("VMWare");

        Customer storedCustomer = testEntityManager.persistAndFlush(customer);

        assert customerRepository.findById(storedCustomer.getPhoneNo()).isPresent() && storedCustomer.getEmail().equals(customer.getEmail());
    }

    @Test
    public void when_requesting_with_id_should_return_customer() {
        Customer customer = new Customer();
        customer.setName("Spring Boot");
        customer.setPhoneNo("+1122334455");
        customer.setEmail("test@springboot.com");
        customer.setAddress("VMWare");

        Customer storedCustomer = testEntityManager.persistAndFlush(customer);

        assert customerRepository.findById(storedCustomer.getPhoneNo()).isPresent();
    }

    @Test
    public void when_deleting_should_return_noting() {
        Customer customer = new Customer();
        customer.setName("Spring Boot Test");
        customer.setPhoneNo("+000111222333");
        customer.setEmail("test@vmware.com");
        customer.setAddress("VMWare");

        Customer storedCustomer = testEntityManager.persistAndFlush(customer);

        testEntityManager.remove(storedCustomer);

        assert customerRepository.findById(storedCustomer.getPhoneNo()).isEmpty();
    }

    @Test
    public void should_return_customer_from_repository() {
        Customer customer = customerRepository.findAll().get(0);

        assert customer != null && customer.getName().equals("Rod Johnson");
    }

    @Test(expected = ConstraintViolationException.class)
    public void should_not_allow_null_customer_address() {
        Customer customer = new Customer();
        customer.setName("Spring Boot");
        customer.setPhoneNo("+1122334455");
        customer.setEmail("test@springboot.com");

        testEntityManager.persistAndFlush(customer);
    }

    @Test(expected = PersistenceException.class)
    public void should_throw_persistence_exception() {
        Supplier supplier = new Supplier();
        supplier.setName("Test supplier");
        supplier.setPhoneNo("+123456789");
        supplier.setAddress("Localhost");

        testEntityManager.persistAndFlush(supplier);
    }
}
