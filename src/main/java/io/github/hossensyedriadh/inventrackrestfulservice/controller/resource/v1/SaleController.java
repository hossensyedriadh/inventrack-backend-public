package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Sale;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.service.sale.SaleService;
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
@RequestMapping(value = "/v1/sales", produces = {MediaTypes.HAL_JSON_VALUE})
public class SaleController {
    private final SaleService saleService;

    @Autowired
    public SaleController(SaleService saleService) {
        this.saleService = saleService;
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
    public ResponseEntity<?> sales(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<Sale> salePage = saleService.saleOrders(pageable);

        if (salePage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(salePage);

        List<EntityModel<Sale>> saleEntityModels = new ArrayList<>();

        for (int i = 0; i < salePage.getContent().size(); i += 1) {
            Sale sale = saleService.sale(salePage.getContent().get(i).getId());

            EntityModel<Sale> saleEntityModel = EntityModel.of(sale);

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass())
                            .salesByCustomer("+0123456789123", 0, size, Sale.class.getDeclaredFields()[1].getName(), sort[1]))
                    .withRel("sales-by-customer").withTitle("Get sales by customer").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(sale.getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Sale()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(sale))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

            saleEntityModels.add(saleEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sales", saleEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(page, size,
                        Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.SELF)
                .withTitle("Current page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (salePage.getTotalPages() - 1) && salePage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(0, size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.FIRST)
                    .withTitle("First page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(page - 1, size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.PREVIOUS)
                    .withTitle("Previous page").withType(HttpMethod.GET.toString()));
        }

        if (page < (salePage.getTotalPages() - 1) && salePage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(page + 1, size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.NEXT)
                    .withTitle("Next page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales((salePage.getTotalPages() - 1), size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.LAST)
                    .withTitle("Last page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping(value = "/by-customer", params = {"customer"})
    public ResponseEntity<?> salesByCustomer(@RequestParam("customer") String customerPhone,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                             @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<Sale> salePage = saleService.saleOrders(pageable, customerPhone);

        if (salePage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(salePage);

        List<EntityModel<Sale>> saleEntityModels = new ArrayList<>();

        for (int i = 0; i < salePage.getContent().size(); i += 1) {
            Sale sale = saleService.sale(salePage.getContent().get(i).getId());

            EntityModel<Sale> saleEntityModel = EntityModel.of(sale);

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(sale.getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Sale()))
                    .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

            saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(sale))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

            saleEntityModels.add(saleEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("sales", saleEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(customerPhone, page, size,
                        Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.SELF)
                .withTitle("Current page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (salePage.getTotalPages() - 1) && salePage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(customerPhone, 0, size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.FIRST)
                    .withTitle("First page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(customerPhone, page - 1, size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.PREVIOUS)
                    .withTitle("Previous page").withType(HttpMethod.GET.toString()));
        }

        if (page < (salePage.getTotalPages() - 1) && salePage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(customerPhone, page + 1, size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.NEXT)
                    .withTitle("Next page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer(customerPhone,
                            (salePage.getTotalPages() - 1), size,
                            Sale.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.LAST)
                    .withTitle("Last page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> sale(@PathVariable("id") String id) {
        Sale sale = saleService.sale(id);

        EntityModel<Sale> saleEntityModel = EntityModel.of(sale);

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(0, defaultPageSize,
                        Sale.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("sales").withTitle("Get sales").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer("+0123456789123", 0, defaultPageSize,
                        Sale.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("sales").withTitle("Get sales by Customer").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(id))
                .withRel(IanaLinkRelations.SELF).withTitle("Get sale").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Sale()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(sale))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

        return new ResponseEntity<>(saleEntityModel, HttpStatus.OK);
    }

    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> add(@Valid @RequestBody Sale sale) {
        Sale addedSale = saleService.add(sale);

        EntityModel<Sale> saleEntityModel = EntityModel.of(addedSale);

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(0, defaultPageSize,
                        Sale.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("sales").withTitle("Get sales").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer("+0123456789123", 0, defaultPageSize,
                        Sale.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("sales").withTitle("Get sales by Customer").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(addedSale.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Sale()))
                .withRel(IanaLinkRelations.SELF).withTitle("Add sale").withType(HttpMethod.POST.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(sale))
                .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

        return new ResponseEntity<>(saleEntityModel, HttpStatus.CREATED);
    }

    @PutMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestBody Sale sale) {
        Sale updatedSale = saleService.update(sale);

        EntityModel<Sale> saleEntityModel = EntityModel.of(updatedSale);

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sales(0, defaultPageSize,
                        Sale.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("sales").withTitle("Get sales").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).salesByCustomer("+0123456789123", 0, defaultPageSize,
                        Sale.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("sales").withTitle("Get sales by Customer").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).sale(sale.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get sale").withType(HttpMethod.GET.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).add(new Sale()))
                .withRel(IanaLinkRelations.CREATE_FORM).withTitle("Add sale").withType(HttpMethod.POST.toString()));

        saleEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(sale))
                .withRel(IanaLinkRelations.SELF).withTitle("Update sale").withType(HttpMethod.PUT.toString()));

        return new ResponseEntity<>(saleEntityModel, HttpStatus.OK);
    }
}
