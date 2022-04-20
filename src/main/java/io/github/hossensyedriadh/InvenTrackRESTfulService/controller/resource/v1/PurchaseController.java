package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PurchaseOrderModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.purchase.PurchaseService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
@RestController
@RequestMapping(value = "/v1/purchases", produces = {MediaTypes.HAL_JSON_VALUE})
public class PurchaseController {
    private final PurchaseService purchaseService;

    private int defaultPageSize;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
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
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=10&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=1&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=2&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/purchases/?page=1&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=2&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=2&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/purchases/?page=2&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=1&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get purchase orders", description = "Returns paged list of sortable purchase order elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "purchaseCache")
    @GetMapping("/")
    public ResponseEntity<?> orders(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<PurchaseOrderModel> orderPage = purchaseService.purchaseOrders(pageable);

        if (orderPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(orderPage);

        List<EntityModel<PurchaseOrderModel>> orderEntityModels = new ArrayList<>();

        for (int i = 0; i < orderPage.getContent().size(); i += 1) {
            if (purchaseService.purchaseOrder(orderPage.getContent().get(i).getId()).isPresent()) {
                PurchaseOrderModel order = purchaseService.purchaseOrder(orderPage.getContent().get(i).getId()).get();

                EntityModel<PurchaseOrderModel> orderEntityModel = EntityModel.of(order);

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, order.getId()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, order))
                        .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, null, order))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

                orderEntityModels.add(orderEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, page, size,
                        PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, 0, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, page - 1, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page")
                    .withType(HttpMethod.GET.toString()));
        }

        if (page < (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, page + 1, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, orderPage.getTotalPages() - 1, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page")
                    .withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "_links": {
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=0&size=10&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=1&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=1&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=2&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=0&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/purchases/by-supplier?supplier=+12345678912&page=1&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "orders": [
                                    {
                                        "id": "410289c701bc50dea104",
                                        "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                        "category": "Motherboard",
                                        "specifications": null,
                                        "quantity": 3,
                                        "totalPurchasePrice": 119500.0,
                                        "shippingCosts": 2000.0,
                                        "otherCosts": 1000.0,
                                        "sellingPricePerUnit": 72000.0,
                                        "supplier": {
                                            "id": "9470c5d83c1da092",
                                            "name": "XYZ Computers",
                                            "phone": "+12345678901",
                                            "email": "xyz@test.com",
                                            "address": "Singapore",
                                            "website": null,
                                            "notes": null,
                                            "addedOn": "2022-01-10T21:53:21",
                                            "addedBy": "syedriadhhossen",
                                            "updatedOn": null,
                                            "updatedBy": null
                                        },
                                        "status": "PENDING",
                                        "addedBy": "syedriadhhossen",
                                        "addedOn": "2022-01-10T21:53:21",
                                        "updatedBy": null,
                                        "updatedOn": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                                "title": "Get order",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Add order",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/purchases/",
                                                "title": "Update order",
                                                "type": "PATCH"
                                            },
                                            "restock": {
                                                "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                                "title": "Restock Product",
                                                "type": "POST"
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
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get purchase orders by Supplier phone", description = "Returns paged list of sortable purchase order elements by Supplier phone",
            parameters = {@Parameter(name = "supplier"), @Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "purchaseCache")
    @GetMapping(value = "/by-supplier", params = {"supplier"})
    public ResponseEntity<?> ordersBySupplier(HttpServletRequest request, @RequestParam("supplier") String supplierPhone,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<PurchaseOrderModel> orderPage = purchaseService.purchaseOrders(pageable, supplierPhone);

        if (orderPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(orderPage);

        List<EntityModel<PurchaseOrderModel>> orderEntityModels = new ArrayList<>();

        for (int i = 0; i < orderPage.getContent().size(); i += 1) {
            if (purchaseService.purchaseOrder(orderPage.getContent().get(i).getId()).isPresent()) {
                PurchaseOrderModel order = purchaseService.purchaseOrder(orderPage.getContent().get(i).getId()).get();

                EntityModel<PurchaseOrderModel> orderEntityModel = EntityModel.of(order);

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, order.getId()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, order))
                        .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, null, order))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

                orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(request, null, order))
                        .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

                orderEntityModels.add(orderEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, supplierPhone, page, size,
                        PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, supplierPhone, 0, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, supplierPhone, page - 1, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page")
                    .withType(HttpMethod.GET.toString()));
        }

        if (page < (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, supplierPhone, page + 1, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, supplierPhone,
                            orderPage.getTotalPages() - 1, size,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page")
                    .withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "410289c701bc50dea104",
                                "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                "category": "Motherboard",
                                "specifications": null,
                                "quantity": 3,
                                "totalPurchasePrice": 119500.0,
                                "shippingCosts": 2000.0,
                                "otherCosts": 1000.0,
                                "sellingPricePerUnit": 72000.0,
                                "supplier": {
                                    "id": "9470c5d83c1da092",
                                    "name": "XYZ Computers",
                                    "phone": "+12345678901",
                                    "email": "xyz@test.com",
                                    "address": "Singapore",
                                    "website": null,
                                    "notes": null,
                                    "addedOn": "2022-01-10T21:53:21",
                                    "addedBy": "syedriadhhossen",
                                    "updatedOn": null,
                                    "updatedBy": null
                                },
                                "status": "PENDING",
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-10T21:53:21",
                                "updatedBy": null,
                                "updatedOn": null,
                                "_links": {
                                    "purchases": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=10&sort=id,asc",
                                        "title": "Get orders",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                        "title": "Get order",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Add order",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Update order",
                                        "type": "PATCH"
                                    },
                                    "restock": {
                                        "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                        "title": "Restock Product",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get purchase order by ID",
            description = "Returns a purchase order by given ID", parameters = {@Parameter(name = "id", required = true)})
    @Cacheable(value = "purchaseCache", key = "#id")
    @GetMapping(params = {"id"})
    public ResponseEntity<?> order(HttpServletRequest request, @RequestParam("id") String id) {
        Optional<PurchaseOrderModel> order = purchaseService.purchaseOrder(id);

        if (order.isPresent()) {
            EntityModel<PurchaseOrderModel> purchaseOrderEntityModel = EntityModel.of(order.get());

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, "supplier_phone", 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, id))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get order").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, order.get()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, null, order.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(request, null, order.get()))
                    .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Purchase order not found with id: " + id, HttpStatus.NO_CONTENT, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "410289c701bc50dea104",
                                "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                "category": "Motherboard",
                                "specifications": null,
                                "quantity": 3,
                                "totalPurchasePrice": 119500.0,
                                "shippingCosts": 2000.0,
                                "otherCosts": 1000.0,
                                "sellingPricePerUnit": 72000.0,
                                "supplier": {
                                    "id": "9470c5d83c1da092",
                                    "name": "XYZ Computers",
                                    "phone": "+12345678901",
                                    "email": "xyz@test.com",
                                    "address": "Singapore",
                                    "website": null,
                                    "notes": null,
                                    "addedOn": "2022-01-10T21:53:21",
                                    "addedBy": "syedriadhhossen",
                                    "updatedOn": null,
                                    "updatedBy": null
                                },
                                "status": "PENDING",
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-10T21:53:21",
                                "updatedBy": null,
                                "updatedOn": null,
                                "_links": {
                                    "purchases": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=10&sort=id,asc",
                                        "title": "Get orders",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                        "title": "Get order",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Add order",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Update order",
                                        "type": "PATCH"
                                    },
                                    "restock": {
                                        "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                        "title": "Restock Product",
                                        "type": "POST"
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
                                "message": "Failed to add new purchase order",
                                "error": "Internal Server Error",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            })),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Cancelled orders can not be added",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Add new purchase order",
            description = "Creates and returns new purchase order using given fields")
    @Cacheable(value = "purchaseCache")
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(HttpServletRequest request, @RequestBody PurchaseOrderModel purchaseOrderModel) {
        Optional<PurchaseOrderModel> createdOrder = purchaseService.addPurchaseOrder(purchaseOrderModel);

        if (createdOrder.isPresent()) {
            EntityModel<PurchaseOrderModel> purchaseOrderEntityModel = EntityModel.of(createdOrder.get());

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders")
                    .withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request, "supplier_phone", 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, createdOrder.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new PurchaseOrderModel()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Add order").withType(HttpMethod.POST.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, null, createdOrder.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(request, null, createdOrder.get()))
                    .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.CREATED);
        }

        throw new ResourceCrudException("Failed to add new purchase order", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "410289c701bc50dea104",
                                "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                "category": "Motherboard",
                                "specifications": null,
                                "quantity": 3,
                                "totalPurchasePrice": 119500.0,
                                "shippingCosts": 2000.0,
                                "otherCosts": 1000.0,
                                "sellingPricePerUnit": 72000.0,
                                "supplier": {
                                    "id": "9470c5d83c1da092",
                                    "name": "XYZ Computers",
                                    "phone": "+12345678901",
                                    "email": "xyz@test.com",
                                    "address": "Singapore",
                                    "website": null,
                                    "notes": null,
                                    "addedOn": "2022-01-10T21:53:21",
                                    "addedBy": "syedriadhhossen",
                                    "updatedOn": null,
                                    "updatedBy": null
                                },
                                "status": "IN_STOCK",
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-10T21:53:21",
                                "updatedBy": "syedriadhhossen",
                                "updatedOn": "2022-01-11T10:25:28",
                                "_links": {
                                    "purchases": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=10&sort=id,asc",
                                        "title": "Get orders",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                        "title": "Get order",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Add order",
                                        "type": "POST"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Update order",
                                        "type": "PATCH"
                                    },
                                    "restock": {
                                        "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                        "title": "Restock Product",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Purchase order not found with id: 9a855493c108b49d",
                                "path": "/api/v1/purchases/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Only Pending purchase orders can be updated",
                                "path": "/api/v1/purchases/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Product not found with ID: 9a855493c108b49d",
                                "path": "/api/v1/purchases/9a855493c108b49d"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Update a restock order", description = "Updates provided updatable fields and returns the updated restock order")
    @CachePut(value = "purchaseCache", key = "#purchaseOrderModel.id")
    @PatchMapping(value = "/{product}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @PathVariable(value = "product") String productId,
                                    @RequestBody PurchaseOrderModel purchaseOrderModel) {
        Optional<PurchaseOrderModel> updatePurchaseOrder = purchaseService.updatePurchaseOrder(purchaseOrderModel, productId);

        if (updatePurchaseOrder.isPresent()) {
            EntityModel<PurchaseOrderModel> purchaseOrderEntityModel = EntityModel.of(updatePurchaseOrder.get());

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .ordersBySupplier(request, "supplier_phone", 0, defaultPageSize,
                                    PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, updatePurchaseOrder.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new PurchaseOrderModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, updatePurchaseOrder.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(request, null, updatePurchaseOrder.get()))
                    .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Purchase order not found with id: " + purchaseOrderModel.getId(), HttpStatus.BAD_REQUEST,
                request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "410289c701bc50dea104",
                                "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                "category": "Motherboard",
                                "specifications": null,
                                "quantity": 3,
                                "totalPurchasePrice": 119500.0,
                                "shippingCosts": 2000.0,
                                "otherCosts": 1000.0,
                                "sellingPricePerUnit": 72000.0,
                                "supplier": {
                                    "id": "9470c5d83c1da092",
                                    "name": "XYZ Computers",
                                    "phone": "+12345678901",
                                    "email": "xyz@test.com",
                                    "address": "Singapore",
                                    "website": null,
                                    "notes": null,
                                    "addedOn": "2022-01-10T21:53:21",
                                    "addedBy": "syedriadhhossen",
                                    "updatedOn": null,
                                    "updatedBy": null
                                },
                                "status": "IN_STOCK",
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-10T21:53:21",
                                "updatedBy": "syedriadhhossen",
                                "updatedOn": "2022-01-11T10:25:28",
                                "_links": {
                                    "purchases": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=10&sort=id,asc",
                                        "title": "Get orders",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                        "title": "Get order",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Add order",
                                        "type": "POST"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Update order",
                                        "type": "PATCH"
                                    },
                                    "restock": {
                                        "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                        "title": "Restock Product",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Purchase order not found with id: 9a855493c108b49d",
                                "path": "/api/v1/purchases/"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Only Pending purchase orders can be updated",
                                "path": "/api/v1/purchases/"
                            }
                            """)
            }))
    })
    @Operation(method = "PUT", summary = "Update a purchase order", description = "Updates provided updatable fields and returns the updated order")
    @CachePut(value = "purchaseCache", key = "#purchaseOrderModel.id")
    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody PurchaseOrderModel purchaseOrderModel) {
        Optional<PurchaseOrderModel> updatePurchaseOrder = purchaseService.updatePurchaseOrder(purchaseOrderModel, null);

        if (updatePurchaseOrder.isPresent()) {
            EntityModel<PurchaseOrderModel> purchaseOrderEntityModel = EntityModel.of(updatePurchaseOrder.get());

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .ordersBySupplier(request, "supplier_phone", 0, defaultPageSize,
                                    PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, updatePurchaseOrder.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new PurchaseOrderModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, updatePurchaseOrder.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(request, null, updatePurchaseOrder.get()))
                    .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Purchase order not found with id: " + purchaseOrderModel.getId(), HttpStatus.BAD_REQUEST,
                request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "410289c701bc50dea104",
                                "productName": "Asus ROG Maximus Z690 Formula 12th GEN ATX",
                                "category": "Motherboard",
                                "specifications": null,
                                "quantity": 3,
                                "totalPurchasePrice": 119500.0,
                                "shippingCosts": 2000.0,
                                "otherCosts": 1000.0,
                                "sellingPricePerUnit": 72000.0,
                                "supplier": {
                                    "id": "9470c5d83c1da092",
                                    "name": "XYZ Computers",
                                    "phone": "+12345678901",
                                    "email": "xyz@test.com",
                                    "address": "Singapore",
                                    "website": null,
                                    "notes": null,
                                    "addedOn": "2022-01-10T21:53:21",
                                    "addedBy": "syedriadhhossen",
                                    "updatedOn": null,
                                    "updatedBy": null
                                },
                                "status": "IN_STOCK",
                                "addedBy": "syedriadhhossen",
                                "addedOn": "2022-01-10T21:53:21",
                                "updatedBy": "syedriadhhossen",
                                "updatedOn": "2022-01-11T10:25:28",
                                "_links": {
                                    "purchases": {
                                        "href": "https://localhost:8443/api/v1/purchases/?page=0&size=10&sort=id,asc",
                                        "title": "Get orders",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/purchases?id=410289c701bc50dea104",
                                        "title": "Get order",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Add order",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/purchases/",
                                        "title": "Update order",
                                        "type": "PATCH"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/purchases/restock-product/9a855493c108b49d",
                                        "title": "Restock Product",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/purchases/restock-product/9a855493c108b49d"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Product not found with ID: 9a855493c108b49d",
                                "path": "/api/v1/purchases/restock-product/9a855493c108b49d"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Cancelled orders can not be added",
                                "path": "/api/v1/purchases/restock-product/9a855493c108b49d"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "An active restock request exists for product: 9a855493c108b49d",
                                "path": "/api/v1/purchases/restock-product/9a855493c108b49d"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Create a product restock order", description = "Creates a new purchase order to restock a product")
    @CachePut(value = "purchaseCache", key = "#purchaseOrderModel.id")
    @PostMapping(value = "/restock-product/{product}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> restock(HttpServletRequest request, @PathVariable("product") String productId, @RequestBody PurchaseOrderModel purchaseOrderModel) {
        Optional<PurchaseOrderModel> purchaseOrder = purchaseService.createProductRestockRequest(purchaseOrderModel, productId);

        if (purchaseOrder.isPresent()) {
            EntityModel<PurchaseOrderModel> purchaseOrderEntityModel = EntityModel.of(purchaseOrder.get());

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(request, 0, defaultPageSize,
                            PurchaseOrderModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(request,
                            "supplier_phone", 0, defaultPageSize, PurchaseOrderModel.class.getDeclaredFields()[1].getName(),
                            Sort.DEFAULT_DIRECTION.toString().toLowerCase())).withRel("purchases").withTitle("Get orders by Supplier")
                    .withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(request, purchaseOrder.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new PurchaseOrderModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, null, purchaseOrder.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

            purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(request, productId, purchaseOrder.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Restock Product").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Purchase order not found with id: " + purchaseOrderModel.getId(), HttpStatus.BAD_REQUEST,
                request.getRequestURI());
    }
}
