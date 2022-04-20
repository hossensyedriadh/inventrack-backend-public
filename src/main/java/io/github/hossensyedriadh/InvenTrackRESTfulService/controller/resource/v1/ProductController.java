package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.Authority;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.ProductModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.pojo.PageInfo;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.product.ProductService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@SuppressWarnings("all")
@RestController
@RequestMapping(value = "/v1/products", produces = {MediaTypes.HAL_JSON_VALUE})
public class ProductController {
    private final ProductService productService;

    private final CurrentAuthenticationContext authenticationContext;

    private int defaultPageSize;

    @Autowired
    public ProductController(ProductService productService, CurrentAuthenticationContext authenticationContext) {
        this.productService = productService;
        this.authenticationContext = authenticationContext;
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
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=10&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 10,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "number": 0
                                },
                                "products": [
                                    {
                                        "id": "9a855493c108b49d",
                                        "name": "Intel Core i9 12900K",
                                        "category": "PROCESSOR",
                                        "specifications": null,
                                        "stock": 1,
                                        "price": 60000.0,
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
                                            "updatedBy": null,
                                        },
                                        "images": [
                                            "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                            "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                        ],
                                        "updatedBy": "syedriadhhossen",
                                        "updatedOn": "2022-01-03T22:47:57",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                                "title": "Get product",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/products/",
                                                "title": "Update product",
                                                "type": "PATCH"
                                            },
                                            "update-images": {
                                                "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                                "title": "Update/add product images",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/products/?page=1&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/products/?page=2&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 0
                                },
                                "products": [
                                    {
                                        "id": "9a855493c108b49d",
                                        "name": "Intel Core i9 12900K",
                                        "category": "PROCESSOR",
                                        "specifications": null,
                                        "stock": 1,
                                        "price": 60000.0,
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
                                            "updatedBy": null,
                                        },
                                        "images": [
                                            "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                            "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                        ],
                                        "updatedBy": "syedriadhhossen",
                                        "updatedOn": "2022-01-03T22:47:57",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                                "title": "Get product",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/products/",
                                                "title": "Update product",
                                                "type": "PATCH"
                                            },
                                            "update-images": {
                                                "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                                "title": "Update/add product images",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/products/?page=1&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=1&sort=id,asc",
                                        "title": "First Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=1&sort=id,asc",
                                        "title": "Previous Page",
                                        "type": "GET"
                                    },
                                    "next": {
                                        "href": "https://localhost:8443/api/v1/products/?page=2&size=1&sort=id,asc",
                                        "title": "Next Page",
                                        "type": "GET"
                                    },
                                    "last": {
                                        "href": "https://localhost:8443/api/v1/products/?page=2&size=1&sort=id,asc",
                                        "title": "Last Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 1
                                },
                                "products": [
                                    {
                                        "id": "9a855493c108b49d",
                                        "name": "Intel Core i9 12900K",
                                        "category": "PROCESSOR",
                                        "specifications": null,
                                        "stock": 1,
                                        "price": 60000.0,
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
                                            "updatedBy": null,
                                        },
                                        "images": [
                                            "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                            "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                        ],
                                        "updatedBy": "syedriadhhossen",
                                        "updatedOn": "2022-01-03T22:47:57",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                                "title": "Get product",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/products/",
                                                "title": "Update product",
                                                "type": "PATCH"
                                            },
                                            "update-images": {
                                                "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                                "title": "Update/add product images",
                                                "type": "POST"
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
                                        "href": "https://localhost:8443/api/v1/products/?page=2&size=1&sort=id,asc",
                                        "title": "Current Page",
                                        "type": "GET"
                                    },
                                    "first": {
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=1&sort=id,asc",
                                        "title": "First Page",
                                        "type": "GET"
                                    },
                                    "previous": {
                                        "href": "https://localhost:8443/api/v1/products/?page=1&size=1&sort=id,asc",
                                        "title": "Previous Page",
                                        "type": "GET"
                                    }
                                },
                                "page": {
                                    "size": 1,
                                    "totalElements": 3,
                                    "totalPages": 3,
                                    "number": 2
                                },
                                "products": [
                                    {
                                        "id": "9a855493c108b49d",
                                        "name": "Intel Core i9 12900K",
                                        "category": "PROCESSOR",
                                        "specifications": null,
                                        "stock": 1,
                                        "price": 60000.0,
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
                                            "updatedBy": null,
                                        },
                                        "images": [
                                            "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                            "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                        ],
                                        "updatedBy": "syedriadhhossen",
                                        "updatedOn": "2022-01-03T22:47:57",
                                        "_links": {
                                            "item": {
                                                "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                                "title": "Get product",
                                                "type": "GET"
                                            },
                                            "edit-form": {
                                                "href": "https://localhost:8443/api/v1/products/",
                                                "title": "Update product",
                                                "type": "PATCH"
                                            },
                                            "update-images": {
                                                "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                                "title": "Update/add product images",
                                                "type": "POST"
                                            }
                                        }
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get products", description = "Returns paged list of sortable product elements",
            parameters = {@Parameter(name = "page"), @Parameter(name = "size"), @Parameter(name = "sort")})
    @Cacheable(value = "productCache")
    @GetMapping("/")
    public ResponseEntity<?> products(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<ProductModel> productPage = productService.getProducts(pageable);

        if (productPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(productPage);

        List<EntityModel<ProductModel>> productEntityModels = new ArrayList<>();

        for (int i = 0; i < productPage.getContent().size(); i += 1) {
            if (productService.getProduct(productPage.getContent().get(i).getId()).isPresent()) {
                ProductModel product = productService.getProduct(productPage.getContent().get(i).getId()).get();

                if (authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                    product.setPurchaseOrder(null);
                }

                EntityModel<ProductModel> productEntityModel = EntityModel.of(product);

                productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(request, product.getId()))
                        .withRel(IanaLinkRelations.ITEM).withTitle("Get product").withType(HttpMethod.GET.toString()));

                if (!authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                    productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, product))
                            .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

                    productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, product.getId(), null))
                            .withRel("update-images").withTitle("Update/add product images").withType(HttpMethod.POST.toString()));
                }

                productEntityModels.add(productEntityModel);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("products", productEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, page, size,
                        ProductModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.SELF)
                .withTitle("Current Page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (productPage.getTotalPages() - 1) && productPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, 0, size,
                            ProductModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.FIRST)
                    .withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, page - 1, size,
                            ProductModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.PREVIOUS)
                    .withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (productPage.getTotalPages() - 1) && productPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, page + 1, size,
                            ProductModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.NEXT)
                    .withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, (productPage.getTotalPages() - 1), size,
                            ProductModel.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.LAST)
                    .withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                [
                                    {
                                        "id": "9a855493c108b49d",
                                        "name": "Intel Core i9 12900K",
                                        "category": "PROCESSOR",
                                        "specifications": null,
                                        "stock": 1,
                                        "price": 60000.0,
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
                                            "updatedBy": null,
                                        },
                                        "images": [
                                            "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                            "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                        ],
                                        "updatedBy": "syedriadhhossen",
                                        "updatedOn": "2022-01-03T22:47:57"
                                    }
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get products list", description = "Returns list product elements")
    @GetMapping(value = "/unpaged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsUnpaged() {
        List<ProductModel> products = productService.getProducts();

        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "9a855493c108b49d",
                                "name": "Intel Core i9 12900K",
                                "category": "PROCESSOR",
                                "specifications": null,
                                "stock": 1,
                                "price": 60000.0,
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
                                    "updatedBy": null,
                                },
                                "images": [
                                    "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                    "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                ],
                                "updatedBy": "syedriadhhossen",
                                "updatedOn": "2022-01-03T22:47:57",
                                "_links": {
                                    "products": {
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=10&sort=id,asc",
                                        "title": "Get products",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                        "title": "Get product",
                                        "type": "GET"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/products/",
                                        "title": "Update product",
                                        "type": "PATCH"
                                    },
                                    "update-images": {
                                        "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                        "title": "Update/add product images",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get product by ID",
            description = "Returns a product by given ID", parameters = {@Parameter(name = "id", required = true)})
    @Cacheable(value = "productCache", key = "#id")
    @GetMapping(params = {"id"})
    public ResponseEntity<?> product(HttpServletRequest request, @RequestParam("id") String id) {
        Optional<ProductModel> product = productService.getProduct(id);

        if (product.isPresent()) {
            ProductModel productModel = product.get();
            if (authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                productModel.setPurchaseOrder(null);
            }

            EntityModel<ProductModel> productEntityModel = EntityModel.of(productModel);

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, 0, defaultPageSize,
                            ProductModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase())).withRel("products")
                    .withMedia(MediaTypes.HAL_JSON_VALUE).withTitle("Get products").withType(HttpMethod.GET.toString()));

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(request, id))
                    .withRel(IanaLinkRelations.SELF).withTitle("Get product").withType(HttpMethod.GET.toString()));

            if (!authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, productModel))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

                productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, id, null))
                        .withRel("update-images").withTitle("Update/add product images").withType(HttpMethod.POST.toString()));
            }

            return new ResponseEntity<>(productEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Product not found with id: " + id, HttpStatus.NO_CONTENT, request.getRequestURI());
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "9a855493c108b49d",
                                "name": "Intel Core i9 12900K",
                                "category": "PROCESSOR",
                                "specifications": null,
                                "stock": 1,
                                "price": 60000.0,
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
                                    "updatedBy": null,
                                },
                                "images": [
                                    "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                    "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                ],
                                "updatedBy": "syedriadhhossen",
                                "updatedOn": "2022-01-03T22:47:57",
                                "_links": {
                                    "products": {
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=10&sort=id,asc",
                                        "title": "Get products",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                        "title": "Get product",
                                        "type": "GET"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/products/",
                                        "title": "Update product",
                                        "type": "PATCH"
                                    },
                                    "update-images": {
                                        "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                        "title": "Update/add product images",
                                        "type": "POST"
                                    }
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 400, message = "Bad Request", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Product not found with id: 9a855493c108b49d",
                                "error": "Bad Request",
                                "path": "/api/v1/products/"
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
                                "path": "/api/v1/products/"
                            }
                            """)
            }))
    })
    @Operation(method = "PATCH", summary = "Update product information",
            description = "Updates a product information by finding the product using the id " +
                    "given in the body and updates updatable information as given in the body")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @CachePut(value = "productCache", key = "#productModel.id")
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody ProductModel productModel) {
        Optional<ProductModel> product = productService.updateProduct(productModel);

        if (product.isPresent()) {
            EntityModel<ProductModel> productEntityModel = EntityModel.of(product.get());

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, 0, defaultPageSize,
                            ProductModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                    .withRel("products").withType(HttpMethod.GET.toString()).withTitle("Get products"));

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(request, product.get().getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get product").withType(HttpMethod.GET.toString()));

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, product.get()))
                    .withRel(IanaLinkRelations.SELF).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request,
                            product.get().getId(), null))
                    .withRel("update-images").withTitle("Update/add product images").withType(HttpMethod.POST.toString()));

            return new ResponseEntity<>(productEntityModel, HttpStatus.OK);
        }

        throw new ResourceCrudException("Product not found with id: " + productModel.getId(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }


    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaTypes.HAL_JSON_VALUE, value = """
                            {
                                "id": "9a855493c108b49d",
                                "name": "Intel Core i9 12900K",
                                "category": "PROCESSOR",
                                "specifications": null,
                                "stock": 1,
                                "price": 60000.0,
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
                                    "updatedBy": null,
                                },
                                "images": [
                                    "https://pay.internationalglobalnetwork.com/wp-content/uploads/woocommerce-placeholder.png",
                                    "https://www.notebookcheck.net/fileadmin/Notebooks/News/_nc3/3358356_l_a.jpg"
                                ],
                                "updatedBy": "syedriadhhossen",
                                "updatedOn": "2022-01-03T22:47:57",
                                "_links": {
                                    "products": {
                                        "href": "https://localhost:8443/api/v1/products/?page=0&size=10&sort=id,asc",
                                        "title": "Get products",
                                        "type": "GET"
                                    },
                                    "item": {
                                        "href": "https://localhost:8443/api/v1/products?id=9a855493c108b49d",
                                        "title": "Get product",
                                        "type": "GET"
                                    },
                                    "edit-form": {
                                        "href": "https://localhost:8443/api/v1/products/",
                                        "title": "Update product",
                                        "type": "PATCH"
                                    },
                                    "self": {
                                        "href": "https://localhost:8443/api/v1/products/images/9470c5d83c1da092",
                                        "title": "Update/add product images",
                                        "type": "POST"
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
                                "message": "Product not found with id: 9a855493c108b49d",
                                "error": "Bad Request",
                                "path": "/api/v1/products/images/9a855493c108b49d"
                            }
                            """),
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "status": 400,
                                "timestamp": "31-12-2021 12:59:00",
                                "message": "Maximum no. of allowed files is 2 and minimum is 1",
                                "error": "Bad Request",
                                "path": "/api/v1/products/images/9a855493c108b49d"
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
                                "path": "/api/v1/products/images/9a855493c108b49d"
                            }
                            """)
            }))
    })
    @Operation(method = "POST", summary = "Add/update product images",
            description = "Replaces existing images (if any) with new images sent as multipart files. " +
                    "A maximum number of 2 files can be added, each having maximum size of 5MB. Allowed formats are jpeg and png. " +
                    "Images crossing bounds will be ignored")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @CachePut(value = "productCache", key = "#id")
    @PostMapping("/images/{id}")
    public ResponseEntity<?> updateImages(HttpServletRequest request, @PathVariable("id") String id,
                                          @RequestPart("image") MultipartFile[] files) {
        if (files.length > 0 && files.length <= 2) {
            boolean isValid = false;
            for (int i = 0; i < files.length; i += 1) {
                if (!files[i].isEmpty()) {
                    long size = files[i].getSize() / (1024L * 1024L);
                    if (size <= 5) {
                        String type = Objects.requireNonNull(files[i].getContentType()).toLowerCase();
                        if (type.equals("image/png") || type.equals("image/jpg") || type.equals("image/jpeg")) {
                            isValid = true;
                        } else {
                            throw new ResourceCrudException("File- " + files[i].getOriginalFilename() + " is not accepted. Only PNG / JPG / JPEG files are allowed",
                                    HttpStatus.NOT_ACCEPTABLE, request.getRequestURI());
                        }
                    } else {
                        throw new ResourceCrudException("File- " + files[i].getOriginalFilename() + " exceeds size boundary. Maximum allowed file size is 5 MB",
                                HttpStatus.NOT_ACCEPTABLE, request.getRequestURI());
                    }
                } else {
                    throw new ResourceCrudException("File- " + files[i].getOriginalFilename() + " is empty",
                            HttpStatus.NOT_ACCEPTABLE, request.getRequestURI());
                }
            }

            if (isValid) {
                Optional<ProductModel> product = productService.updateProductImages(id, files);

                if (product.isPresent()) {
                    EntityModel<ProductModel> productEntityModel = EntityModel.of(product.get());

                    productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(request, 0, defaultPageSize,
                                    ProductModel.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                            .withRel("products").withType(HttpMethod.GET.toString()).withTitle("Get products"));

                    productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(request, id))
                            .withRel(IanaLinkRelations.ITEM).withTitle("Get product").withType(HttpMethod.GET.toString()));

                    productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(request, product.get()))
                            .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

                    productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, id, null))
                            .withRel(IanaLinkRelations.SELF).withTitle("Update/add product images").withType(HttpMethod.POST.toString()));

                    return new ResponseEntity<>(productEntityModel, HttpStatus.OK);
                }

                throw new ResourceCrudException("Product not found with id: " + id, HttpStatus.BAD_REQUEST, request.getRequestURI());
            }
        }

        throw new ResourceCrudException("Maximum no. of allowed files is 2 and minimum is 1", HttpStatus.BAD_REQUEST, request.getRequestURI());
    }
}
