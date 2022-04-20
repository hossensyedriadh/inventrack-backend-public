package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.CustomerModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.customer.CustomerService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=0&size=10&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "customers": [
                                    {
                                        "name": "Lorem Ipsum",
                                        "phone": "+12345678901",
                                        "email": null,
                                        "address": "Manhattan, New York, USA",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/customers?id=821be4ba004294c7",
                                                "title": "Get customer",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/customers/",
                                                "title": "Update customer",
                                                "type": "PATCH"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "number": 0
                                }
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=0&size=1&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=1&size=1&sort=name,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=2&size=1&sort=name,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "customers": [
                                    {
                                        "name": "John Doe",
                                        "phone": "+13456789012",
                                        "email": "john@test.com",
                                        "address": "Brooklyn, New York, USA",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/customers?phone=+12345678912",
                                                "title": "Get customer",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/customers/",
                                                "title": "Update customer",
                                                "type": "PATCH"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 0
                                }
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=1&size=1&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=0&size=1&sort=name,asc",
                                        "title": "First Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=0&size=1&sort=name,asc",
                                        "title": "Previous Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=1&size=1&sort=name,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=2&size=1&sort=name,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "customers": [
                                    {
                                        "name": "John Doe",
                                        "phone": "+13456789012",
                                        "email": "john@test.com",
                                        "address": "Brooklyn, New York, USA",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/customers?phone=+12345678912",
                                                "title": "Get customer",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/customers/",
                                                "title": "Update customer",
                                                "type": "PATCH"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 1
                                }
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=2&size=1&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=0&size=1&sort=name,asc",
                                        "title": "First Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=1&size=1&sort=name,asc",
                                        "title": "Previous Page",
                                        "type": "GET"
                                    }
                                },
                                "customers": [
                                    {
                                        "name": "Lorem Ipsum",
                                        "phone": "+12345678901",
                                        "email": null,
                                        "address": "Manhattan, New York, USA",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/customers?phone=+12345678912",
                                                "title": "Get customer",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/customers/",
                                                "title": "Update customer",
                                                "type": "PATCH"
                                            }
                                        }
                                    }
                                ],
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 2
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get customers",
            description = "Returns paged list of sortable customer elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "customerCache")
    @GetMapping("/")
    public ResponseEntity<?> customers(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "sort", defaultValue = "name,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<CustomerModel> customerPage = customerService.getCustomers(pageable);

        if (customerPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(customerPage);

        List<EntityModel<CustomerModel>> customerEntityModels = new ArrayList<>();

        for (int i = 0; i < customerPage.getContent().size(); i += 1) {
            if (customerService.getCustomer(customerPage.getContent().get(i).getPhone()).isPresent()) {
                CustomerModel customer = customerService.getCustomer(customerPage.getContent().get(i).getPhone()).get();

                EntityModel<CustomerModel> customerEntityModel = EntityModel.of(customer);

                customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customer(request, customer.getPhone()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get customer").withType(HttpMethod.GET.toString()));

                customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, customer))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update customer").withType(HttpMethod.PATCH.toString()));

                customerEntityModels.add(customerEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, page, size,
                        CustomerModel.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (customerPage.getTotalPages() - 1) && customerPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, 0, size,
                            CustomerModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, page - 1, size,
                            CustomerModel.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (customerPage.getTotalPages() - 1) && customerPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, page + 1, size,
                            CustomerModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, customerPage.getTotalPages() - 1, size,
                            CustomerModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "name": "Lorem Ipsum",
                                "phone": "+12345678901",
                                "email": null,
                                "address": "Manhattan, New York, USA",
                                "_links": {
                                    "customers": {
                                        "href": "https://localhost:8443/api/v1/customers/?page=0&size=10&sort=name,asc",
                                        "title": "Get customers",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/customers?phone=+12345678912",
                                        "title": "Get customer",
                                        "type": "GET"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/customers/",
                                        "title": "Update customer",
                                        "type": "PATCH"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get customer by phone no.", description = "Returns a customer by given phone no. Phone number must be URL encoded",
            parameters = {@Parameter(name = "phone", required = true)})
    @Cacheable(value = "customerCache", key = "#phone")
    @GetMapping(params = {"phone"})
    public ResponseEntity<?> customer(HttpServletRequest request, @RequestParam("phone") String phone) {
        Optional<CustomerModel> customer = customerService.getCustomer(phone);

        if (customer.isPresent()) {
            EntityModel<CustomerModel> customerEntityModel = EntityModel.of(customer.get());

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, 0, defaultPageSize,
                            CustomerModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("customers").withTitle("Get customers")
                    .withType(HttpMethod.GET.toString()));

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customer(request, phone))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get customer").withType(HttpMethod.GET.toString()));

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, customer.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update customer").withType(HttpMethod.PATCH.toString()));

            return new ResponseEntity<>(customerEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Customer not found with phone: " + phone, HttpStatus.NO_CONTENT, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "name": "John Doe",
                                "phone": "+13456789012",
                                "email": "john@test.com",
                                "address": "Brooklyn, New York, USA",
                                "_links": {
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/customers?phone=+12345678912",
                                        "title": "Get customer",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/customers/",
                                        "title": "Update customer",
                                        "type": "PATCH"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Customer not found with phone: +12345678912",
                                "error": "Bad Request",
                                "path": "/api/v1/customers"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Update customer information",
            description = "Updates a customer information by finding the customer using the phone " +
                    "given in the body and updates updatable information as given in the body")
    @CachePut(value = "customerCache", key = "#customer.phone")
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody CustomerModel customer) {
        Optional<CustomerModel> updatedCustomer = customerService.updateCustomer(customer);

        if (updatedCustomer.isPresent()) {
            EntityModel<CustomerModel> customerEntityModel = EntityModel.of(updatedCustomer.get());

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customers(request, 0, defaultPageSize,
                            CustomerModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("customers").withTitle("Get customers").withType(HttpMethod.GET.toString()));

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).customer(request, updatedCustomer.get().getPhone()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get customer").withType(HttpMethod.GET.toString()));

            customerEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, updatedCustomer.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update customer").withType(HttpMethod.PATCH.toString()));

            return new ResponseEntity<>(customerEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Customer not found with phone: " + customer.getPhone(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }
}
