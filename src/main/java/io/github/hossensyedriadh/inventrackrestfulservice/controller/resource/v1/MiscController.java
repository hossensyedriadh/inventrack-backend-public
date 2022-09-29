package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.DeliveryMedium;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.PaymentMethod;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.ProductCategory;
import io.github.hossensyedriadh.inventrackrestfulservice.service.misc.MiscService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/misc", produces = {MediaType.APPLICATION_JSON_VALUE})
public class MiscController {
    private final MiscService miscService;

    @Autowired
    public MiscController(MiscService miscService) {
        this.miscService = miscService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/product-categories")
    public ResponseEntity<?> productCategories() {
        List<ProductCategory> categories = miscService.getProductCategories();

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping(value = "/product-category", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addProductCategory(@Valid @RequestBody ProductCategory productCategory) {
        ProductCategory category = miscService.addProductCategory(productCategory);

        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<?> paymentMethods() {
        List<PaymentMethod> methods = miscService.getPaymentMethods();

        if (methods.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(methods, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping(value = "/payment-method", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addPaymentMethod(@Valid @RequestBody PaymentMethod paymentMethod) {
        PaymentMethod method = miscService.addPaymentMethod(paymentMethod);

        return new ResponseEntity<>(method, HttpStatus.CREATED);
    }

    @GetMapping("/delivery-mediums")
    public ResponseEntity<?> deliveryMediums() {
        List<DeliveryMedium> mediums = miscService.getDeliveryMediums();

        if (mediums.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(mediums, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping(value = "/delivery-medium", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> addDeliveryMedium(@Valid @RequestBody DeliveryMedium deliveryMedium) {
        DeliveryMedium medium = miscService.addDeliveryMedium(deliveryMedium);

        return new ResponseEntity<>(medium, HttpStatus.CREATED);
    }
}
