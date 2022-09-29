package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Product;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.Authority;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.model.PageInfo;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import io.github.hossensyedriadh.inventrackrestfulservice.service.product.ProductService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(value = "/v1/products", produces = {MediaTypes.HAL_JSON_VALUE})
public class ProductController {
    private final ProductService productService;
    private final CurrentAuthenticationContext currentAuthenticationContext;
    private final HttpServletRequest request;

    @Autowired
    public ProductController(ProductService productService, CurrentAuthenticationContext currentAuthenticationContext,
                             HttpServletRequest request) {
        this.productService = productService;
        this.currentAuthenticationContext = currentAuthenticationContext;
        this.request = request;
    }

    private int defaultPageSize;

    @Value("${spring.data.rest.default-page-size}")
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/")
    public ResponseEntity<?> products(@RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @RequestParam(value = "sort", defaultValue = "id,asc") String... sort) {
        List<Sort.Order> sortOrders = new ArrayList<>();

        sortOrders.add(new Sort.Order(Sort.Direction.fromOptionalString(sort[1]).orElse(Sort.Direction.ASC), sort[0]));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortOrders));

        Page<Product> productPage = productService.products(pageable);

        if (productPage.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageInfo pageInfo = new PageInfo(productPage);

        List<EntityModel<Product>> productEntityModels = new ArrayList<>();

        for (int i = 0; i < productPage.getContent().size(); i += 1) {
            Product product = productService.product(productPage.getContent().get(i).getId());

            if (currentAuthenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                product.setPurchaseOrder(null);
            }

            EntityModel<Product> productEntityModel = EntityModel.of(product);

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(product.getId()))
                    .withRel(IanaLinkRelations.ITEM).withTitle("Get product").withType(HttpMethod.GET.toString()));

            if (!currentAuthenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(product))
                        .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

                productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, product.getId(), new MultipartFile[]{}))
                        .withRel("update-images").withTitle("Update/add product images").withType(HttpMethod.POST.toString()));
            }

            productEntityModels.add(productEntityModel);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("products", productEntityModels);
        response.put("page", pageInfo);

        EntityModel<?> responseModel = EntityModel.of(response);

        responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(page, size,
                        Product.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.SELF)
                .withTitle("Current Page").withType(HttpMethod.GET.toString()));

        if (page > 0 && page <= (productPage.getTotalPages() - 1) && productPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(0, size,
                            Product.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.FIRST)
                    .withTitle("First Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(page - 1, size,
                            Product.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.PREVIOUS)
                    .withTitle("Previous Page").withType(HttpMethod.GET.toString()));
        }

        if (page < (productPage.getTotalPages() - 1) && productPage.getTotalPages() > 1) {
            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(page + 1, size,
                            Product.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.NEXT)
                    .withTitle("Next Page").withType(HttpMethod.GET.toString()));

            responseModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products((productPage.getTotalPages() - 1), size,
                            Product.class.getDeclaredFields()[1].getName(), sort[1])).withRel(IanaLinkRelations.LAST)
                    .withTitle("Last Page").withType(HttpMethod.GET.toString()));
        }

        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @GetMapping(value = "/unpaged", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductsUnpaged() {
        List<Product> products = productService.products();

        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> product(@PathVariable("id") String id) {
        Product product = productService.product(id);

        if (currentAuthenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
            product.setPurchaseOrder(null);
        }

        EntityModel<Product> productEntityModel = EntityModel.of(product);

        productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(0, defaultPageSize,
                        Product.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase())).withRel("products")
                .withMedia(MediaTypes.HAL_JSON_VALUE).withTitle("Get products").withType(HttpMethod.GET.toString()));

        productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(id))
                .withRel(IanaLinkRelations.SELF).withTitle("Get product").withType(HttpMethod.GET.toString()));

        if (!currentAuthenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(product))
                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, id, new MultipartFile[]{}))
                    .withRel("update-images").withTitle("Update/add product images").withType(HttpMethod.POST.toString()));
        }

        return new ResponseEntity<>(productEntityModel, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PatchMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> update(@Valid @RequestBody Product product) {
        Product updatedProduct = productService.update(product);

        EntityModel<Product> productEntityModel = EntityModel.of(updatedProduct);

        productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(0, defaultPageSize,
                        Product.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                .withRel("products").withType(HttpMethod.GET.toString()).withTitle("Get products"));

        productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(product.getId()))
                .withRel(IanaLinkRelations.ITEM).withTitle("Get product").withType(HttpMethod.GET.toString()));

        productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(product))
                .withRel(IanaLinkRelations.SELF).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

        productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, product.getId(), new MultipartFile[]{}))
                .withRel("update-images").withTitle("Update/add product images").withType(HttpMethod.POST.toString()));

        return new ResponseEntity<>(productEntityModel, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ROOT', 'ROLE_ADMINISTRATOR')")
    @PostMapping("/images/{id}")
    public ResponseEntity<?> updateImages(HttpServletRequest request, @PathVariable("id") String id,
                                          @RequestPart("image") MultipartFile[] files) {
        if (files.length > 0 && files.length <= 2) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    long size = file.getSize() / (1024L * 1024L);
                    if (size <= 5) {
                        String type = Objects.requireNonNull(file.getContentType()).toLowerCase();
                        if (type.equals("image/png") || type.equals("image/jpg") || type.equals("image/jpeg")) {
                            Product product = productService.updateImages(id, files);
                            EntityModel<Product> productEntityModel = EntityModel.of(product);

                            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).products(0, defaultPageSize,
                                            Product.class.getDeclaredFields()[1].getName(), Sort.DEFAULT_DIRECTION.toString().toLowerCase()))
                                    .withRel("products").withType(HttpMethod.GET.toString()).withTitle("Get products"));

                            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).product(id))
                                    .withRel(IanaLinkRelations.ITEM).withTitle("Get product").withType(HttpMethod.GET.toString()));

                            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).update(product))
                                    .withRel(IanaLinkRelations.EDIT_FORM).withTitle("Update product").withType(HttpMethod.PATCH.toString()));

                            productEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).updateImages(request, id, new MultipartFile[]{}))
                                    .withRel(IanaLinkRelations.SELF).withTitle("Update/add product images").withType(HttpMethod.POST.toString()));

                            return new ResponseEntity<>(productEntityModel, HttpStatus.OK);
                        } else {
                            throw new ResourceException("File- " + file.getOriginalFilename() + " is not accepted. Only PNG / JPG / JPEG files are allowed",
                                    HttpStatus.NOT_ACCEPTABLE, request);
                        }
                    } else {
                        throw new ResourceException("File- " + file.getOriginalFilename() + " exceeds size boundary. Maximum allowed file size is 5 MB",
                                HttpStatus.NOT_ACCEPTABLE, request);
                    }
                } else {
                    throw new ResourceException("File- " + file.getOriginalFilename() + " is empty",
                            HttpStatus.NOT_ACCEPTABLE, request);
                }
            }
        }

        throw new ResourceException("Maximum no. of allowed files is 2 and minimum is 1", HttpStatus.BAD_REQUEST, request);
    }
}
