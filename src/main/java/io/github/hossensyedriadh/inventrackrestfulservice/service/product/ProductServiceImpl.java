package io.github.hossensyedriadh.inventrackrestfulservice.service.product;

import io.github.hossensyedriadh.inventrackrestfulservice.configuration.cloud.OracleCloudObjectStorage;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.Product;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.ProductImage;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.Authority;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProductImageRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProductRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CurrentAuthenticationContext authenticationContext;
    private final OracleCloudObjectStorage oracleCloudObjectStorage;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductImageRepository productImageRepository,
                              CurrentAuthenticationContext authenticationContext,
                              OracleCloudObjectStorage oracleCloudObjectStorage, HttpServletRequest httpServletRequest) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.authenticationContext = authenticationContext;
        this.oracleCloudObjectStorage = oracleCloudObjectStorage;
        this.httpServletRequest = httpServletRequest;
    }

    private static final String productsFolderName = "products";

    private List<String> getProductImages(String productId) {
        return productImageRepository.findAll().stream()
                .filter(image -> image.getForProduct().getId().equals(productId))
                .map(ProductImage::getUrl).toList();
    }

    @Override
    public Page<Product> products(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);

        return new PageImpl<>(productPage.stream()
                .peek(product -> product.setImages(this.getProductImages(product.getId()))).toList(), pageable,
                productPage.getTotalElements());
    }

    @Override
    public List<Product> products() {
        return productRepository.findAll().stream().peek(product -> product.setImages(this.getProductImages(product.getId()))).toList();
    }

    @Override
    public Product product(String id) {
        if (productRepository.findById(id).isPresent()) {
            Product product = productRepository.findById(id).get();
            if (this.authenticationContext.getAuthenticatedUser().getAuthority().equals(Authority.ROLE_MODERATOR)) {
                product.setPurchaseOrder(null);
            }
            product.setImages(this.getProductImages(id));
            return product;
        }

        throw new ResourceException("Product not found with ID: " + id, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public Product update(Product product) {
        if (productRepository.findById(product.getId()).isPresent()) {
            product.setUpdatedBy(this.authenticationContext.getAuthenticatedUser());
            return productRepository.saveAndFlush(product);
        }

        throw new ResourceException("Product not found with ID: " + product.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public Product updateImages(String productId, MultipartFile[] images) {
        if (productRepository.findById(productId).isPresent()) {
            Product product = productRepository.findById(productId).get();

            List<ProductImage> currentImages = productImageRepository.findAll().stream()
                    .filter(image -> image.getForProduct().getId().equals(productId)).toList();

            List<ProductImage> productImages = new ArrayList<>();
            String[] links = this.storeImages(images, productId);

            for (int i = 0; i < images.length; i += 1) {
                ProductImage image = new ProductImage();
                image.setUrl(links[i]);
                image.setForProduct(product);
                productImages.add(image);
            }

            productImageRepository.deleteAll(currentImages);
            productImageRepository.saveAllAndFlush(productImages);

            product.setImages(productImageRepository.findAll().stream().filter(image -> image.getForProduct().getId().equals(productId))
                    .map(ProductImage::getUrl).toList());

            return product;
        }

        throw new ResourceException("Product not found with ID: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    private String[] storeImages(MultipartFile[] images, String productId) {
        String[] urls = new String[images.length];

        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Product", productId);

        for (int i = 0; i < images.length; i += 1) {
            String url = this.oracleCloudObjectStorage.uploadFile(productsFolderName,
                    productId.concat("_" + (i + 1)), metadata, images[i]);

            urls[i] = url;
        }

        return urls;
    }
}
