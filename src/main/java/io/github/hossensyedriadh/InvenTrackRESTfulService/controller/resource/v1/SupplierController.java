package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SupplierModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.supplier.SupplierService;
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
@RequestMapping(value = "/v1/suppliers", produces = {MediaTypes.HAL_JSON_VALUE})
public class SupplierController {
    private final SupplierService supplierService;
    private int defaultPageSize;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
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
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=10&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "suppliers": [
                                    {
                                        "name": "XYZ Computers",
                                        "phone": "+12345678901",
                                        "email": "xyz@test.com",
                                        "address": "Singapore",
                                        "website": null,
                                        "notes": null,
                                        "addedOn": "2022-01-10T21:53:21",
                                        "addedBy": "syedriadhhossen",
                                        "updatedOn": null,
                                        "updatedBy": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                                "title": "Get supplier",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Add supplier",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Update supplier",
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
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=1&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=1&size=1&sort=name,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=2&size=1&sort=name,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "suppliers": [
                                    {
                                        "name": "XYZ Computers",
                                        "phone": "+12345678901",
                                        "email": "xyz@test.com",
                                        "address": "Singapore",
                                        "website": null,
                                        "notes": null,
                                        "addedOn": "2022-01-10T21:53:21",
                                        "addedBy": "syedriadhhossen",
                                        "updatedOn": null,
                                        "updatedBy": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                                "title": "Get supplier",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Add supplier",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Update supplier",
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
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=1&size=1&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=1&sort=name,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=1&sort=name,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=2&size=1&sort=name,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=2&size=1&sort=name,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "suppliers": [
                                    {
                                        "name": "XYZ Computers",
                                        "phone": "+12345678901",
                                        "email": "xyz@test.com",
                                        "address": "Singapore",
                                        "website": null,
                                        "notes": null,
                                        "addedOn": "2022-01-10T21:53:21",
                                        "addedBy": "syedriadhhossen",
                                        "updatedOn": null,
                                        "updatedBy": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                                "title": "Get supplier",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Add supplier",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Update supplier",
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
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=2&size=1&sort=name,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=0&sort=name,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=1&sort=name,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "suppliers": [
                                    {
                                        "name": "XYZ Computers",
                                        "phone": "+12345678901",
                                        "email": "xyz@test.com",
                                        "address": "Singapore",
                                        "website": null,
                                        "notes": null,
                                        "addedOn": "2022-01-10T21:53:21",
                                        "addedBy": "syedriadhhossen",
                                        "updatedOn": null,
                                        "updatedBy": null,
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                                "title": "Get supplier",
                                                "type": "GET"
                                            },
                                            "create-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Add supplier",
                                                "type": "POST"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/suppliers/",
                                                "title": "Update supplier",
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
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 403, message = "Forbidden", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "You do not have permission to access this resource",
                                "path": "/api/v1/suppliers/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get suppliers", description = "Returns paged list of sortable supplier elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "supplierCache")
    @GetMapping("/")
    public ResponseEntity<?> suppliers(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "sort", defaultValue = "name,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<SupplierModel> supplierPage = supplierService.getSuppliers(pageable);

        if (supplierPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(supplierPage);

        List<EntityModel<SupplierModel>> supplierEntityModels = new ArrayList<>();

        for (int i = 0; i < supplierPage.getContent().size(); i += 1) {
            if (supplierService.getSupplier(supplierPage.getContent().get(i).getPhone()).isPresent()) {
                SupplierModel supplier = supplierService.getSupplier(supplierPage.getContent().get(i).getPhone()).get();

                EntityModel<SupplierModel> supplierEntityModel = EntityModel.of(supplier);

                supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(request, supplier.getPhone()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

                supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SupplierModel()))
                        .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

                supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, supplier))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

                supplierEntityModels.add(supplierEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("suppliers", supplierEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, page, size,
                        SupplierModel.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (supplierPage.getTotalPages() - 1) && supplierPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, 0, size,
                            SupplierModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, page - 1, size,
                            SupplierModel.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (supplierPage.getTotalPages() - 1) && supplierPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, page + 1, size,
                            SupplierModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, supplierPage.getTotalPages() - 1, size,
                            SupplierModel.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                [
                                    {
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
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get suppliers list", description = "Returns list of suppliers elements")
    @GetMapping(value = "/unpaged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSuppliersUnpaged() {
        List<SupplierModel> suppliers = supplierService.getSuppliers();

        if (suppliers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "name": "XYZ Computers",
                                "phone": "+12345678901",
                                "email": "xyz@test.com",
                                "address": "Singapore",
                                "website": null,
                                "notes": null,
                                "addedOn": "2022-01-10T21:53:21",
                                "addedBy": "syedriadhhossen",
                                "updatedOn": null,
                                "updatedBy": null,
                                "_links": {
                                    "suppliers": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=10&sort=name,asc",
                                        "title": "Get suppliers",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                        "title": "Get supplier",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/suppliers/",
                                        "title": "Add supplier",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/suppliers/",
                                        "title": "Update supplier",
                                        "type": "PATCH"
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
                                "path": "/api/v1/suppliers/"
                            }
                            """)
            }))
    })
    @Operation(method = "GET", summary = "Get supplier by phone no.",
            description = "Returns a supplier by given phone.", parameters = {@Parameter(name = "phone", required = true)})
    @Cacheable(value = "supplierCache", key = "#phone")
    @GetMapping(params = {"phone"})
    public ResponseEntity<?> supplier(HttpServletRequest request, @RequestParam("phone") String phone) {
        Optional<SupplierModel> supplier = supplierService.getSupplier(phone);

        if (supplier.isPresent()) {
            EntityModel<SupplierModel> supplierEntityModel = EntityModel.of(supplier.get());

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, 0, defaultPageSize,
                            SupplierModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("suppliers").withTitle("Get suppliers").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(request, phone))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SupplierModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, supplier.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

            return new ResponseEntity<>(supplierEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Supplier not found with phone: " + phone, HttpStatus.NO_CONTENT, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "name": "XYZ Computers",
                                "phone": "+12345678901",
                                "email": "xyz@test.com",
                                "address": "Singapore",
                                "website": null,
                                "notes": null,
                                "addedOn": "2022-01-10T21:53:21",
                                "addedBy": "syedriadhhossen",
                                "updatedOn": null,
                                "updatedBy": null,
                                "_links": {
                                    "suppliers": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=10&sort=name,asc",
                                        "title": "Get suppliers",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                        "title": "Get supplier",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/suppliers/",
                                        "title": "Add supplier",
                                        "type": "POST"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/suppliers/",
                                        "title": "Update supplier",
                                        "type": "PATCH"
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
                                "message": "Failed to add new supplier",
                                "error": "Internal Server Error",
                                "path": "/api/v1/suppliers/"
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
                                "path": "/api/v1/suppliers/"
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "timestamp": "2022-01-14T22:10:30.731+00:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Supplier already exists with this phone number: +12345678912",
                                "path": "/api/v1/suppliers/"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Add a new supplier", description = "Adds and returns a new supplier")
    @Cacheable(value = "supplierCache")
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(HttpServletRequest request, @RequestBody SupplierModel supplier) {
        Optional<SupplierModel> updatedSupplier = supplierService.addSupplier(supplier);

        if (updatedSupplier.isPresent()) {
            EntityModel<SupplierModel> supplierEntityModel = EntityModel.of(updatedSupplier.get());

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, 0, defaultPageSize,
                            SupplierModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("suppliers").withTitle("Get suppliers").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(request, updatedSupplier.get().getPhone()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SupplierModel()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, updatedSupplier.get()))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

            return new ResponseEntity<>(supplierEntityModel, HttpStatus.CREATED);
        }

        throw new ResourceCrudException("Failed to add new supplier", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "name": "XYZ Computers",
                                "phone": "+12345678901",
                                "email": "xyz@test.com",
                                "address": "Singapore",
                                "website": null,
                                "notes": null,
                                "addedOn": "2022-01-10T21:53:21",
                                "addedBy": "syedriadhhossen",
                                "updatedOn": null,
                                "updatedBy": null,
                                "_links": {
                                    "suppliers": {
                                        "href": "https://localhost:8443/api/v1/suppliers/?page=0&size=10&sort=name,asc",
                                        "title": "Get suppliers",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/suppliers?phone=+12345678912",
                                        "title": "Get supplier",
                                        "type": "GET"
                                    },
                                    "create-form": {
                                        "href": "https://localhost:8443/api/v1/suppliers/",
                                        "title": "Add supplier",
                                        "type": "POST"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/suppliers/",
                                        "title": "Update supplier",
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
                                "message": "Supplier not found with phone: +12345678912",
                                "error": "Bad Request",
                                "path": "/api/v1/suppliers/"
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
                                "path": "/api/v1/suppliers/"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Update a supplier",
            description = "Updates a supplier using updatable fields and returns the updated order")
    @CachePut(value = "supplierCache", key = "#supplier.phone")
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody SupplierModel supplier) {
        Optional<SupplierModel> updatedSupplier = supplierService.updateSupplier(supplier);

        if (updatedSupplier.isPresent()) {
            EntityModel<SupplierModel> supplierEntityModel = EntityModel.of(updatedSupplier.get());

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(request, 0, defaultPageSize,
                            SupplierModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("suppliers").withTitle("Get suppliers").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(request, updatedSupplier.get().getPhone()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(request, new SupplierModel()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, updatedSupplier.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

            return new ResponseEntity<>(supplierEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Supplier not found with phone: " + supplier.getPhone(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }
}
