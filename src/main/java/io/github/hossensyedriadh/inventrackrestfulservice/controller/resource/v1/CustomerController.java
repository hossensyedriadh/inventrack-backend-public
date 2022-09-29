package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Customer;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.service.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/customers", produces = {MediaTypes.HAL_JSON_VALUE})
public class CustomerController {
    private final CustomerService customerService;
    private int defaultPageSize;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/")
    public ResponseEntity<?> customers(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "sort", defaultValue = "name,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<Customer> customerPage = customerService.customers(pageable);

        if (customerPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(customerPage);

        List<EntityModel<Customer>> customerEntityModels = new ArrayList<>();

        for (int i = 0; i < customerPage.getContent().size(); i += 1) {
            Customer customer = customerService.customer(customerPage.getContent().get(i).getPhoneNo());

            EntityModel<Customer> customerEntityModel = EntityModel.of(customer);

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customer(customer.getPhoneNo()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get customer").withType(HttpMethod.GET.toString()));

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(customer))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update customer").withType(HttpMethod.PUT.toString()));

            customerEntityModels.add(customerEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(page, size,
                        Customer.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (customerPage.getTotalPages() - 1) && customerPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(0, size,
                            Customer.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(page - 1, size,
                            Customer.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (customerPage.getTotalPages() - 1) && customerPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(page + 1, size,
                            Customer.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(customerPage.getTotalPages() - 1, size,
                            Customer.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<?> customer(@PathVariable("phone") String phone) {
        Customer customer = customerService.customer(phone);

        EntityModel<Customer> customerEntityModel = EntityModel.of(customer);

        customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(0, defaultPageSize,
                        Customer.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("customers").withTitle("Get customers")
                .withType(HttpMethod.GET.toString()));

        customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customer(phone))
                .withRel(IanaLinkRelations.SELF).withTitle("Get customer").withType(HttpMethod.GET.toString()));

        customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(customer))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update customer").withType(HttpMethod.PUT.toString()));

        return new ResponseEntity<>(customerEntityModel, HttpStatus.OK);
    }

    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(customer);

        EntityModel<Customer> customerEntityModel = EntityModel.of(updatedCustomer);

        customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(0, defaultPageSize,
                        Customer.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("customers").withTitle("Get customers").withType(HttpMethod.GET.toString()));

        customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customer(updatedCustomer.getPhoneNo()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get customer").withType(HttpMethod.GET.toString()));

        customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(updatedCustomer))
                .withRel(IanaLinkRelations.SELF).withTitle("Update customer").withType(HttpMethod.PUT.toString()));

        return new ResponseEntity<>(customerEntityModel, HttpStatus.OK);
    }
}
