package io.github.hossensyedriadh.InvenTrackRESTfulService.service.product;

import io.github.hossensyedriadh.InvenTrackRESTfulService.model.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public sealed interface ProductService permits ProductServiceImpl {
    Page<ProductModel> getProducts(Pageable pageable);

    List<ProductModel> getProducts();

    Optional<ProductModel> getProduct(String id);

    Optional<ProductModel> updateProduct(ProductModel productModel);

    Optional<ProductModel> updateProductImages(String productId, MultipartFile[] files);
}
