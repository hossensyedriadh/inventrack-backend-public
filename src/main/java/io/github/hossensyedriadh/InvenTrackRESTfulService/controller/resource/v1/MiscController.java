package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.DeliveryMedium;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.PaymentMethod;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.ProductCategory;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.misc.MiscService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/misc", produces = {MediaType.APPLICATION_JSON_VALUE})
public class MiscController {
    private final MiscService miscService;

    @Autowired
    public MiscController(MiscService miscService) {
        this.miscService = miscService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            [
                                {
                                    "name": "Processor"
                                },
                                {
                                    "name": "Motherboard"
                                }
                                ]
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Product Categories", description = "Returns all product categories")
    @GetMapping("/product-categories")
    public ResponseEntity<?> getProductCategories() {
        List<ProductCategory> categories = miscService.getProductCategories();

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "name": "Processor"
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "POST", summary = "Add new product category", description = "Adds and returns new product category")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping(value = "/product-category", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addProductCategory(@RequestBody ProductCategory category) {
        Optional<ProductCategory> productCategory = miscService.addProductCategory(category);

        if (productCategory.isPresent()) {
            return new ResponseEntity<>(productCategory.get(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            [
                                {
                                    "name": "COD"
                                }
                            ]
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Payment Methods", description = "Returns all payment methods")
    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
        List<PaymentMethod> methods = miscService.getPaymentMethods();

        if (methods.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(methods, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "name": "COD"
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "POST", summary = "Add new payment method", description = "Adds and returns new payment method")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping(value = "/payment-method", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPaymentMethod(@RequestBody PaymentMethod method) {
        Optional<PaymentMethod> paymentMethod = miscService.addPaymentMethod(method);

        if (paymentMethod.isPresent()) {
            return new ResponseEntity<>(paymentMethod.get(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            [
                                {
                                    "name": "E-Courier"
                                }
                            ]
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Delivery Mediums", description = "Returns all delivery mediums")
    @GetMapping("/delivery-mediums")
    public ResponseEntity<?> getDeliveryMediums() {
        List<DeliveryMedium> mediums = miscService.getDeliveryMediums();

        if (mediums.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(mediums, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "name": "Pathao Delivery"
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "POST", summary = "Add new delivery medium", description = "Adds and returns new delivery medium")
    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping(value = "/delivery-medium", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDeliveryMedium(@RequestBody DeliveryMedium medium) {
        Optional<DeliveryMedium> deliveryMedium = miscService.addDeliveryMedium(medium);

        if (deliveryMedium.isPresent()) {
            return new ResponseEntity<>(deliveryMedium.get(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
