package io.github.hossensyedriadh.InvenTrackRESTfulService.service.product;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Product;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.ProductImage;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.ProductToModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.ProductModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProductImageRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProductRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public final class ProductServiceImpl implements ProductService {
    private final ObjectFactory<ProductRepository> productRepositoryObjectFactory;
    private final ObjectFactory<ProductImageRepository> productImageRepositoryObjectFactory;
    private final ProductToModel toProductModel;
    private final CurrentAuthenticationContext authenticationContext;
    private final HttpServletRequest httpServletRequest;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${oracle.cloud.credentials.config-file-path}")
    private String ociConfigFilePath;

    @Value("${oracle.cloud.credentials.namespace}")
    private String ociNamespace;

    @Value("${oracle.cloud.object-storage.bucket-name}")
    private String ociBucketName;

    @Value("${oracle.cloud.object-storage.products-folder-name}")
    private String productsFolderName;

    @Autowired
    public ProductServiceImpl(ObjectFactory<ProductRepository> productRepositoryObjectFactory,
                              ObjectFactory<ProductImageRepository> productImageRepositoryObjectFactory,
                              ProductToModel toProductModel, CurrentAuthenticationContext authenticationContext,
                              HttpServletRequest httpServletRequest) {
        this.productRepositoryObjectFactory = productRepositoryObjectFactory;
        this.productImageRepositoryObjectFactory = productImageRepositoryObjectFactory;
        this.toProductModel = toProductModel;
        this.authenticationContext = authenticationContext;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<ProductModel> getProducts(Pageable pageable) {
        Page<Product> productPage = productRepositoryObjectFactory.getObject().findAll(pageable);

        Page<ProductModel> productModelPage = productPage.map(toProductModel::convert);

        for (int i = 0; i < productModelPage.getContent().size(); i += 1) {
            String currentProductId = productModelPage.getContent().get(i).getId();
            productModelPage.getContent().get(i).setImages(this.getProductImages(currentProductId));
        }

        return productModelPage;
    }

    @Override
    public List<ProductModel> getProducts() {
        List<ProductModel> products = productRepositoryObjectFactory.getObject().findAll().stream()
                .map(toProductModel::convert).toList();

        if (!products.isEmpty()) {
            for (ProductModel product : products) {
                String currentProductId = product.getId();
                product.setImages(this.getProductImages(currentProductId));
            }
        }

        return products;
    }

    @Override
    public Optional<ProductModel> getProduct(String id) {
        if (productRepositoryObjectFactory.getObject().findById(id).isPresent()) {
            ProductModel productModel = this.toProductModel.convert(productRepositoryObjectFactory.getObject().getById(id));

            if (productModel != null) {
                if (this.getProductImages(id).size() > 0) {
                    productModel.setImages(this.getProductImages(id));
                }
                return Optional.of(productModel);
            }
            return Optional.empty();
        }

        return Optional.empty();
    }

    private List<String> getProductImages(String productId) {
        List<ProductImage> images = productImageRepositoryObjectFactory.getObject().findAll()
                .stream().filter(image -> image.getForProduct().getId().equals(productId)).toList();
        List<String> productImages = new ArrayList<>();

        for (ProductImage image : images) {
            productImages.add(image.getUrl());
        }

        return productImages;
    }

    @Override
    public Optional<ProductModel> updateProduct(ProductModel productModel) {
        if (productRepositoryObjectFactory.getObject().findById(productModel.getId()).isPresent()) {
            Product product = productRepositoryObjectFactory.getObject().getById(productModel.getId());
            product.setSpecifications((productModel.getSpecifications() != null) ? productModel.getSpecifications() : product.getSpecifications());
            product.setPrice((productModel.getPrice() != null) ? productModel.getPrice() : product.getPrice());
            product.setUpdatedBy(this.authenticationContext.getAuthenticatedUser());
            productRepositoryObjectFactory.getObject().saveAndFlush(product);

            ProductModel updated = this.toProductModel.convert(productRepositoryObjectFactory.getObject().getById(productModel.getId()));

            if (updated != null) {
                if (this.getProductImages(product.getId()).size() > 0) {
                    updated.setImages(this.getProductImages(product.getId()));
                }
                return Optional.of(updated);
            }
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public Optional<ProductModel> updateProductImages(String productId, MultipartFile[] files) {
        if (productRepositoryObjectFactory.getObject().findById(productId).isPresent()) {
            Product product = productRepositoryObjectFactory.getObject().getById(productId);

            List<ProductImage> currentImages = productImageRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(image -> image.getForProduct().getId().equals(productId)).toList();
            productImageRepositoryObjectFactory.getObject().deleteAll(currentImages);

            List<ProductImage> productImages = new ArrayList<>();
            String[] links = this.storeImages(files, product.getId());

            for (int i = 0; i < files.length; i += 1) {
                ProductImage image = new ProductImage();
                image.setUrl(links[i]);
                image.setForProduct(product);
                productImages.add(image);
            }
            productImageRepositoryObjectFactory.getObject().saveAllAndFlush(productImages);

            ProductModel productModel = this.toProductModel.convert(product);
            List<ProductImage> images = productImageRepositoryObjectFactory.getObject().findAll()
                    .stream().filter(image -> image.getForProduct().getId().equals(productId)).toList();

            assert productModel != null;
            if (images.size() > 0) {
                productModel.setImages(this.getProductImages(productId));
            }

            return Optional.of(productModel);
        }

        return Optional.empty();
    }


    private String[] storeImages(MultipartFile[] files, String productId) {
        Logger logger = Logger.getLogger(this.getClass());
        String[] urls = new String[files.length];

        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Product", productId);
        metadata.put("Copyright", "© " + LocalDate.now().getYear() + ", " + applicationName);

        try {
            final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(ociConfigFilePath);
            final ConfigFileAuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            ObjectStorage client = new ObjectStorageClient(provider);
            client.setRegion(Region.AP_SINGAPORE_1);

            UploadConfiguration uploadConfiguration = UploadConfiguration.builder()
                    .allowMultipartUploads(true).allowParallelUploads(true).build();

            UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

            for (int i = 0; i < files.length; i += 1) {
                String objectName = productId.concat("_" + (i + 1));
                metadata.put("Timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));

                String contentType = files[i].getContentType();

                File file = new File(System.getProperty("java.io.tmpdir") + "/" + objectName);
                files[i].transferTo(file);

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucketName(ociBucketName).namespaceName(ociNamespace).objectName(productsFolderName + "/" + objectName)
                        .contentType(contentType).opcMeta(metadata).build();

                UploadManager.UploadRequest uploadRequest = UploadManager.UploadRequest.builder(file)
                        .allowOverwrite(true).build(request);
                UploadManager.UploadResponse uploadResponse = uploadManager.upload(uploadRequest);

                logger.info(uploadResponse);

                String staticUrl = client.getEndpoint().concat("/n/").concat(ociNamespace).concat("/b/").concat(ociBucketName)
                        .concat("/o/").concat(productsFolderName).concat("/").concat(objectName);

                urls[i] = staticUrl;
            }

            return urls;
        } catch (IOException e) {
            logger.error(e);
            throw new ResourceCrudException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, httpServletRequest.getRequestURI());
        }
    }
}
