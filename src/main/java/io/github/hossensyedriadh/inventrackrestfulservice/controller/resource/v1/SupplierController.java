package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Supplier;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.service.supplier.SupplierService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
@RestController
@RequestMapping(value = "/v1/suppliers", produces = {MediaTypes.HAL_JSON_VALUE})
public class SupplierController {
    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    private int defaultPageSize;

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
    public ResponseEntity<?> suppliers(@RequestParam(value = "page", defaultValue = "0") int page,
                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                       @RequestParam(value = "sort", defaultValue = "name,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<Supplier> supplierPage = supplierService.suppliers(pageable);

        if (supplierPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(supplierPage);

        List<EntityModel<Supplier>> supplierEntityModels = new ArrayList<>();

        for (int i = 0; i < supplierPage.getContent().size(); i += 1) {
            Supplier supplier = supplierService.supplier(supplierPage.getContent().get(i).getPhoneNo());

            EntityModel<Supplier> supplierEntityModel = EntityModel.of(supplier);

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(supplier.getPhoneNo()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Supplier()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

            supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(supplier))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

            supplierEntityModels.add(supplierEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("suppliers", supplierEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(page, size,
                        Supplier.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (supplierPage.getTotalPages() - 1) && supplierPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(0, size,
                            Supplier.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(page - 1, size,
                            Supplier.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (supplierPage.getTotalPages() - 1) && supplierPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(page + 1, size,
                            Supplier.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(supplierPage.getTotalPages() - 1, size,
                            Supplier.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping(value = "/unpaged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSuppliersUnpaged() {
        List<Supplier> suppliers = supplierService.suppliers();

        if (suppliers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(suppliers, HttpStatus.OK);
    }

    @GetMapping("/{phone}")
    public ResponseEntity<?> supplier(@PathVariable("phone") String phone) {
        Supplier supplier = supplierService.supplier(phone);

        EntityModel<Supplier> supplierEntityModel = EntityModel.of(supplier);

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(0, defaultPageSize,
                        Supplier.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("suppliers").withTitle("Get suppliers").withType(HttpMethod.GET.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(phone))
                .withRel(IanaLinkRelations.SELF).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Supplier()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(supplier))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

        return new ResponseEntity<>(supplierEntityModel, HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(@Valid @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.add(supplier);

        EntityModel<Supplier> supplierEntityModel = EntityModel.of(updatedSupplier);

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(0, defaultPageSize,
                        Supplier.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("suppliers").withTitle("Get suppliers").withType(HttpMethod.GET.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(updatedSupplier.getPhoneNo()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Supplier()))
                .withRel(IanaLinkRelations.SELF).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(updatedSupplier))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

        return new ResponseEntity<>(supplierEntityModel, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.update(supplier);

        EntityModel<Supplier> supplierEntityModel = EntityModel.of(updatedSupplier);

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).suppliers(0, defaultPageSize,
                        Supplier.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("suppliers").withTitle("Get suppliers").withType(HttpMethod.GET.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).supplier(updatedSupplier.getPhoneNo()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get supplier").withType(HttpMethod.GET.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Supplier()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add supplier").withType(HttpMethod.POST.toString()));

        supplierEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(updatedSupplier))
                .withRel(IanaLinkRelations.SELF).withTitle("Update supplier").withType(HttpMethod.PATCH.toString()));

        return new ResponseEntity<>(supplierEntityModel, HttpStatus.OK);
    }
}
