package io.github.hossensyedriadh.inventrackrestfulservice.service.product;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Page<Product> products(Pageable pageable);

    List<Product> products();

    Product product(String id);

    Product update(Product updatedProduct);

    Product updateImages(String productId, MultipartFile[] images);
}
