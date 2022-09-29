package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.PurchaseOrder;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.service.purchase.PurchaseService;
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
@RequestMapping(value = "/v1/purchases", produces = {MediaTypes.HAL_JSON_VALUE})
public class PurchaseController {
    private final PurchaseService purchaseService;

    @Autowired
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
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
    public ResponseEntity<?> orders(@RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                    @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<PurchaseOrder> orderPage = purchaseService.purchaseOrders(pageable);

        if (orderPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(orderPage);

        List<EntityModel<PurchaseOrder>> orderEntityModels = new ArrayList<>();

        for (int i = 0; i < orderPage.getContent().size(); i += 1) {
            PurchaseOrder order = purchaseService.purchaseOrder(orderPage.getContent().get(i).getId());

            EntityModel<PurchaseOrder> orderEntityModel = EntityModel.of(order);

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(order.getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(order))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(null, order))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

            orderEntityModels.add(orderEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(page, size,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(0, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(page - 1, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page")
                    .withType(HttpMethod.GET.toString()));
        }

        if (page < (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(page + 1, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(orderPage.getTotalPages() - 1, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page")
                    .withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping(value = "/by-supplier", params = {"supplier"})
    public ResponseEntity<?> ordersBySupplier(@RequestParam("supplier") String supplierPhone,
                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                              @RequestParam(value = "size", defaultValue = "10") int size,
                                              @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<PurchaseOrder> orderPage = purchaseService.purchaseOrders(pageable, supplierPhone);

        if (orderPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(orderPage);

        List<EntityModel<PurchaseOrder>> orderEntityModels = new ArrayList<>();

        for (int i = 0; i < orderPage.getContent().size(); i += 1) {
            PurchaseOrder order = purchaseService.purchaseOrder(orderPage.getContent().get(i).getId());

            EntityModel<PurchaseOrder> orderEntityModel = EntityModel.of(order);

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(order.getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(order))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(order))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PUT.toString()));

            orderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(null, order))
                    .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

            orderEntityModels.add(orderEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(supplierPhone, page, size,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                .withRel(IanaLinkRelations.SELF).withTitle("Current Page")
                .withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(supplierPhone, 0, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.FIRST).withTitle("First Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(supplierPhone, page - 1, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.Direction.ASC.toString().toLowerCase()))
                    .withRel(IanaLinkRelations.PREVIOUS).withTitle("Previous Page")
                    .withType(HttpMethod.GET.toString()));
        }

        if (page < (orderPage.getTotalPages() - 1) && orderPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(supplierPhone, page + 1, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.NEXT).withTitle("Next Page")
                    .withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier(supplierPhone,
                            orderPage.getTotalPages() - 1, size,
                            PurchaseOrder.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel(IanaLinkRelations.LAST).withTitle("Last Page")
                    .withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> order(@PathVariable("id") String id) {
        PurchaseOrder order = purchaseService.purchaseOrder(id);

        EntityModel<PurchaseOrder> purchaseOrderEntityModel = EntityModel.of(order);

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(0, defaultPageSize,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier("supplier_phone",
                        0, defaultPageSize,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(id))
                .withRel(IanaLinkRelations.SELF).withTitle("Get order").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(order))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(order))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PUT.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(null, order))
                .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(@Valid @RequestBody PurchaseOrder purchaseOrder) {
        PurchaseOrder createdOrder = purchaseService.addPurchaseOrder(purchaseOrder);

        EntityModel<PurchaseOrder> purchaseOrderEntityModel = EntityModel.of(createdOrder);

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(0, defaultPageSize,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders")
                .withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                        .ordersBySupplier("supplier_phone", 0, defaultPageSize,
                                PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(createdOrder.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new PurchaseOrder()))
                .withRel(IanaLinkRelations.SELF).withTitle("Add order").withType(HttpMethod.POST.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(null, createdOrder))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PUT.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(null, createdOrder))
                .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{product}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@PathVariable(value = "product") String productId,
                                    @Valid @RequestBody PurchaseOrder purchaseOrderModel) {
        PurchaseOrder updatePurchaseOrder = purchaseService.updatePurchaseOrder(purchaseOrderModel, productId);

        EntityModel<PurchaseOrder> purchaseOrderEntityModel = EntityModel.of(updatePurchaseOrder);

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(0, defaultPageSize,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                        .ordersBySupplier("supplier_phone", 0, defaultPageSize,
                                PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(updatePurchaseOrder.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new PurchaseOrder()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(updatePurchaseOrder))
                .withRel(IanaLinkRelations.SELF).withTitle("Update order").withType(HttpMethod.PUT.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(null, updatePurchaseOrder))
                .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
    }

    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestBody PurchaseOrder purchaseOrder) {
        PurchaseOrder updatedPurchaseOrder = purchaseService.updatePurchaseOrder(purchaseOrder, null);

        EntityModel<PurchaseOrder> purchaseOrderEntityModel = EntityModel.of(purchaseOrder);

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(0, defaultPageSize,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                        .ordersBySupplier("supplier_phone", 0, defaultPageSize,
                                PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders by Supplier").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(updatedPurchaseOrder.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new PurchaseOrder()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(updatedPurchaseOrder))
                .withRel(IanaLinkRelations.SELF).withTitle("Update order").withType(HttpMethod.PATCH.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(null, updatedPurchaseOrder))
                .withRel("restock").withTitle("Restock Product").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
    }

    @PostMapping(value = "/restock-product/{product}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> restock(@PathVariable("product") String productId, @Valid @RequestBody PurchaseOrder purchaseOrderModel) {
        PurchaseOrder purchaseOrder = purchaseService.createProductRestockOrder(purchaseOrderModel, productId);

        EntityModel<PurchaseOrder> purchaseOrderEntityModel = EntityModel.of(purchaseOrder);

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).orders(0, defaultPageSize,
                        PurchaseOrder.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("purchases").withTitle("Get orders").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).ordersBySupplier("supplier_phone",
                        0, defaultPageSize, PurchaseOrder.class.getDeclaredFields()[1].getName(),
                        Sort.DEFAULT_DIRECTION.toString().toLowerCase())).withRel("purchases").withTitle("Get orders by Supplier")
                .withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).order(purchaseOrder.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get order").withType(HttpMethod.GET.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new PurchaseOrder()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add order").withType(HttpMethod.POST.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(purchaseOrder))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update order").withType(HttpMethod.PUT.toString()));

        purchaseOrderEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).restock(productId, purchaseOrder))
                .withRel(IanaLinkRelations.SELF).withTitle("Restock Product").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(purchaseOrderEntityModel, HttpStatus.OK);
    }
}
