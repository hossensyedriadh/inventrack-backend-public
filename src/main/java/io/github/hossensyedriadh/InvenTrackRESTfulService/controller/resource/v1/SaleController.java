package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SaleModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.sale.SaleService;
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
@RequestMapping(value = "/v1/sales", produces = {MediaTypes.HAL_JSON_VALUE})
public class SaleController {
    private final SaleService saleService;

    private int defaultPageSize;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
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
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 10,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "number": 0
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=1&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=1&size=1&sort=id,asc",
                                        "title": "Next page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=2&size=1&sort=id,asc",
                                        "title": "Last page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 0
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=1&size=1&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=1&sort=id,asc",
                                        "title": "First page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=1&sort=id,asc",
                                        "title": "Previous page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=2&size=1&sort=id,asc",
                                        "title": "Next page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=2&size=1&sort=id,asc",
                                        "title": "Last page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 1
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=2&size=1&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=1&sort=id,asc",
                                        "title": "First page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=1&size=1&sort=id,asc",
                                        "title": "Previous page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 2
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get sale orders", description = "Returns paged list of sortable sale order elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "saleCache")
    @GetMapping("/")
    public ResponseEntity<?> sales(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<SaleModel> saleModelPage = saleService.getSales(pageable);

        if (saleModelPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(saleModelPage);

        List<EntityModel<SaleModel>> saleEntityModels = new ArrayList<>();

        for (int i = 0; i < saleModelPage.getContent().size(); i += 1) {
            if (saleService.getSale(saleModelPage.getContent().get(i).getId()).isPresent()) {
                SaleModel sale = saleService.getSale(saleModelPage.getContent().get(i).getId()).get();

                EntityModel<SaleModel> saleEntityModel = EntityModel.of(sale);

                saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(request, sale.getId()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

                saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SaleModel()))
                        .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

                saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, sale))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

                saleEntityModels.add(saleEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sales", saleEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, page, size,
                        SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.SELF)
                .withTitle("Current page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (saleModelPage.getTotalPages() - 1) && saleModelPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, 0, size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.FIRST)
                    .withTitle("First page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, page - 1, size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.PREVIOUS)
                    .withTitle("Previous page").withType(HttpMethod.GET.toString()));
        }

        if (page < (saleModelPage.getTotalPages() - 1) && saleModelPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, page + 1, size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.NEXT)
                    .withTitle("Next page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, (saleModelPage.getTotalPages() - 1), size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.LAST)
                    .withTitle("Last page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=0&size=10&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 10,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "number": 0
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=1&size=1&sort=id,asc",
                                        "title": "Next page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=2&size=1&sort=id,asc",
                                        "title": "Last page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 0
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=1&size=1&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "First page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "Previous page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Next page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Last page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 1
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """),
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Current page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "First page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/sales/by-customer?customer=+12345678912&page=1&size=1&sort=id,asc",
                                        "title": "Previous page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 2
                                },
                                "sales": [
                                    {
                                        "id": "4d2886cc9445e846742c",
                                        "products": [
                                            {
                                                "product": {
                                                    "id": "9a855493c108b49d",
                                                    "name": "Intel Core i9 12900K",
                                                    "category": "PROCESSOR",
                                                    "specifications": null,
                                                    "stock": 1,
                                                    "price": 60000.0,
                                                    "images": [
                                                        "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                        "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                                    ],
                                                    "updatedBy": "syedriadhhossen",
                                                    "updatedOn": "2022-01-03T22:47:57"
                                                },
                                                "quantity": 1,
                                                "price": 60000.0
                                            }
                                        ],
                                        "totalPayable": 60000.0,
                                        "totalDue": 60000.0,
                                        "customer": {
                                            "id": "821be4ba004294c7",
                                            "name": "Lorem Ipsum",
                                            "phone": "+1234567901",
                                            "email": null,
                                            "address": "Manhattan, New York, USA"
                                        },
                                        "paymentStatus": "PENDING",
                                        "paymentMethod": "COD",
                                        "paymentDetails": null,
                                        "orderStatus": "CONFIRMED",
                                        "deliveryMedium": "XYZ Delivery",
                                        "notes": null,
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-03T20:25:35",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "sales": {
                                                "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                                "title": "Get sales",
                                                "type": "GET"
                                            },
                                            "self": {
                                                "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                                "title": "Get sale",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Add sale",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/sales/",
                                                "title": "Update sale",
                                                "type": "PUT"
                                            }
                                        }
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get sale orders by customer phone",
            description = "Returns paged list of sortable sale order elements by given customer phone",
            parameters = {@Parameter(name = "customer"), @Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "saleCache")
    @GetMapping(value = "/by-customer", params = {"customer"})
    public ResponseEntity<?> salesByCustomer(HttpServletRequest request, @RequestParam("customer") String customerPhone,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                             @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<SaleModel> saleModelPage = saleService.getSales(pageable, customerPhone);

        if (saleModelPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(saleModelPage);

        List<EntityModel<SaleModel>> saleEntityModels = new ArrayList<>();

        for (int i = 0; i < saleModelPage.getContent().size(); i += 1) {
            if (saleService.getSale(saleModelPage.getContent().get(i).getId()).isPresent()) {
                SaleModel sale = saleService.getSale(saleModelPage.getContent().get(i).getId()).get();

                EntityModel<SaleModel> saleEntityModel = EntityModel.of(sale);

                saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(request, sale.getId()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

                saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SaleModel()))
                        .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

                saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, sale))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

                saleEntityModels.add(saleEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sales", saleEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, customerPhone, page, size,
                        SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.SELF)
                .withTitle("Current page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (saleModelPage.getTotalPages() - 1) && saleModelPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, customerPhone, 0, size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.FIRST)
                    .withTitle("First page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, customerPhone, page - 1, size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.PREVIOUS)
                    .withTitle("Previous page").withType(HttpMethod.GET.toString()));
        }

        if (page < (saleModelPage.getTotalPages() - 1) && saleModelPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, customerPhone, page + 1, size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.NEXT)
                    .withTitle("Next page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, customerPhone,
                            (saleModelPage.getTotalPages() - 1), size,
                            SaleModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.LAST)
                    .withTitle("Last page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "4d2886cc9445e846742c",
                                "products": [
                                    {
                                        "product": {
                                            "id": "9a855493c108b49d",
                                            "name": "Intel Core i9 12900K",
                                            "category": "PROCESSOR",
                                            "specifications": null,
                                            "stock": 1,
                                            "price": 60000.0,
                                            "images": [
                                                "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                            ],
                                            "updatedBy": "syedriadhhossen",
                                            "updatedOn": "2022-01-03T22:47:57"
                                        },
                                        "quantity": 1,
                                        "price": 60000.0
                                    }
                                ],
                                "totalPayable": 60000.0,
                                "totalDue": 60000.0,
                                "customer": {
                                    "id": "821be4ba004294c7",
                                    "name": "Lorem Ipsum",
                                    "phone": "+12345678901",
                                    "email": null,
                                    "address": "Manhattan, New York, USA"
                                },
                                "paymentStatus": "PENDING",
                                "paymentMethod": "COD",
                                "paymentDetails": null,
                                "orderStatus": "CONFIRMED",
                                "deliveryMedium": "XYZ Delivery",
                                "notes": null,
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-03T20:25:35",
                                "updatedBy": null,
                                "updatedOn": null,
                                "_links": {
                                    "sales": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                        "title": "Get sales",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                        "title": "Get sale",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/sales/",
                                        "title": "Add sale",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/sales/",
                                        "title": "Update sale",
                                        "type": "PUT"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get sale order by ID",
            description = "Returns a sale order by given ID", parameters = {@Parameter(name = "id", required = true)})
    @Cacheable(value = "saleCache", key = "#id")
    @GetMapping(params = {"id"})
    public ResponseEntity<?> sale(HttpServletRequest request, @RequestParam("id") String id) {
        Optional<SaleModel> sale = saleService.getSale(id);

        if (sale.isPresent()) {
            EntityModel<SaleModel> saleEntityModel = EntityModel.of(sale.get());

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, 0, defaultPageSize,
                            SaleModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("sales").withTitle("Get sales").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, "customer_phone", 0, defaultPageSize,
                            SaleModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("sales").withTitle("Get sales by Customer").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(request, id))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get sale").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SaleModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, sale.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

            return new ResponseEntity<>(saleEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Sale not found with id: " + id, HttpStatus.NO_CONTENT, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "4d2886cc9445e846742c",
                                "products": [
                                    {
                                        "product": {
                                            "id": "9a855493c108b49d",
                                            "name": "Intel Core i9 12900K",
                                            "category": "PROCESSOR",
                                            "specifications": null,
                                            "stock": 1,
                                            "price": 60000.0,
                                            "images": [
                                                "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                            ],
                                            "updatedBy": "syedriadhhossen",
                                            "updatedOn": "2022-01-03T22:47:57"
                                        },
                                        "quantity": 1,
                                        "price": 60000.0
                                    }
                                ],
                                "totalPayable": 60000.0,
                                "totalDue": 60000.0,
                                "customer": {
                                    "id": "821be4ba004294c7",
                                    "name": "Lorem Ipsum",
                                    "phone": "+12345678901",
                                    "email": null,
                                    "address": "Manhattan, New York, USA"
                                },
                                "paymentStatus": "PENDING",
                                "paymentMethod": "COD",
                                "paymentDetails": null,
                                "orderStatus": "CONFIRMED",
                                "deliveryMedium": "XYZ Delivery",
                                "notes": null,
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-03T20:25:35",
                                "updatedBy": null,
                                "updatedOn": null,
                                "_links": {
                                    "sales": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                        "title": "Get sales",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                        "title": "Get sale",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/",
                                        "title": "Add sale",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/sales/",
                                        "title": "Update sale",
                                        "type": "PUT"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 500, message = "Internal Server Error", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 500,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Failed to add new sale order",
                                "error": "Internal Server Error",
                                "path": "/api/v1/sales/"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "For partial payment, total due must be less than total payable amount",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "For complete payment, total due must be 0",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Not enough stock for product id: 9ac909cbea4",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Product not found with id: 9ac909cbea4",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Cancelled orders should not be added",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Create a new sale order", description = "Creates and returns a new sale order")
    @Cacheable(value = "saleCache")
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(HttpServletRequest request, @RequestBody SaleModel saleModel) {
        Optional<SaleModel> sale = saleService.addSale(saleModel);

        if (sale.isPresent()) {
            EntityModel<SaleModel> saleEntityModel = EntityModel.of(sale.get());

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, 0, defaultPageSize,
                            SaleModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("sales").withTitle("Get sales").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, "customer_phone", 0, defaultPageSize,
                            SaleModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("sales").withTitle("Get sales by Customer").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(request, sale.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SaleModel()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Add sale").withType(HttpMethod.POST.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, sale.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

            return new ResponseEntity<>(saleEntityModel, HttpStatus.CREATED);
        }

        throw new ResourceCrudException("Failed to add new sale order", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "4d2886cc9445e846742c",
                                "products": [
                                    {
                                        "product": {
                                            "id": "9a855493c108b49d",
                                            "name": "Intel Core i9 12900K",
                                            "category": "PROCESSOR",
                                            "specifications": null,
                                            "stock": 1,
                                            "price": 60000.0,
                                            "images": [
                                                "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                                "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                            ],
                                            "updatedBy": "syedriadhhossen",
                                            "updatedOn": "2022-01-03T22:47:57"
                                        },
                                        "quantity": 1,
                                        "price": 60000.0
                                    }
                                ],
                                "totalPayable": 60000.0,
                                "totalDue": 60000.0,
                                "customer": {
                                    "id": "821be4ba004294c7",
                                    "name": "Lorem Ipsum",
                                    "phone": "+12345678901",
                                    "email": null,
                                    "address": "Manhattan, New York, USA"
                                },
                                "paymentStatus": "PENDING",
                                "paymentMethod": "COD",
                                "paymentDetails": null,
                                "orderStatus": "CONFIRMED",
                                "deliveryMedium": "XYZ Delivery",
                                "notes": null,
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-03T20:25:35",
                                "updatedBy": null,
                                "updatedOn": null,
                                "_links": {
                                    "sales": {
                                        "href": "https://localhost:8443/api/v1/sales/?page=0&size=10&sort=id,asc",
                                        "title": "Get sales",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/sales?id=4d2886cc9445e846742c",
                                        "title": "Get sale",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/sales/",
                                        "title": "Add sale",
                                        "type": "POST"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/sales/",
                                        "title": "Update sale",
                                        "type": "PUT"
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
                                "message": "Sale not found with id: 3ab3a29ef8f1c2685b2f",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "For partial payment, total due must be less than total payable amount",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "For complete payment, total due must be 0",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Not enough stock for product id: 9ac909cbea4",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Product not found with id: 9ac909cbea4",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Cancelled Sale records can not be updated",
                                "error": "Bad Request",
                                "path": "/api/v1/sales/"
                            }
                            """)
            }))
    })
    @Operation(method = "PUT", summary = "Update a sale order",
            description = "Updates a sale order using updatable fields and returns the updated order")
    @CachePut(value = "saleCache", key = "#updatedSaleModel.id")
    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody SaleModel updatedSaleModel) {
        Optional<SaleModel> sale = saleService.updateSale(updatedSaleModel);

        if (sale.isPresent()) {
            EntityModel<SaleModel> saleEntityModel = EntityModel.of(sale.get());

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(request, 0, defaultPageSize,
                            SaleModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("sales").withTitle("Get sales").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(request, "customer_phone", 0, defaultPageSize,
                            SaleModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("sales").withTitle("Get sales by Customer").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(request, sale.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SaleModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, sale.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

            return new ResponseEntity<>(saleEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Sale not found with id: " + updatedSaleModel.getId(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }
}
