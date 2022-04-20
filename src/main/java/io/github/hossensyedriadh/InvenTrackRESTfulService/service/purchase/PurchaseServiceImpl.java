package io.github.hossensyedriadh.InvenTrackRESTfulService.service.purchase;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.*;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.FinanceRecordType;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.ProductPurchaseStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.PurchaseOrderType;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.PurchaseOrderToModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.PurchaseOrderModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SupplierModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.*;
import io.github.hossensyedriadh.InvenTrackRESTfulService.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public final class PurchaseServiceImpl implements PurchaseService {
    private final ObjectFactory<PurchaseOrderRepository> purchaseOrderRepositoryObjectFactory;
    private final ObjectFactory<SupplierRepository> supplierRepositoryObjectFactory;
    private final ObjectFactory<ProductRepository> productRepositoryObjectFactory;
    private final ObjectFactory<FinanceRepository> financeRepositoryObjectFactory;
    private final ObjectFactory<ProductCategoryRepository> productCategoryRepositoryObjectFactory;
    private final CurrentAuthenticationContext authenticationContext;
    private final PurchaseOrderToModel toModel;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public PurchaseServiceImpl(ObjectFactory<PurchaseOrderRepository> purchaseOrderRepositoryObjectFactory,
                               ObjectFactory<SupplierRepository> supplierRepositoryObjectFactory,
                               ObjectFactory<ProductRepository> productRepositoryObjectFactory,
                               ObjectFactory<FinanceRepository> financeRepositoryObjectFactory,
                               ObjectFactory<ProductCategoryRepository> productCategoryRepositoryObjectFactory,
                               CurrentAuthenticationContext authenticationContext,
                               PurchaseOrderToModel toModel,
                               HttpServletRequest httpServletRequest) {
        this.purchaseOrderRepositoryObjectFactory = purchaseOrderRepositoryObjectFactory;
        this.supplierRepositoryObjectFactory = supplierRepositoryObjectFactory;
        this.productRepositoryObjectFactory = productRepositoryObjectFactory;
        this.financeRepositoryObjectFactory = financeRepositoryObjectFactory;
        this.productCategoryRepositoryObjectFactory = productCategoryRepositoryObjectFactory;
        this.authenticationContext = authenticationContext;
        this.toModel = toModel;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<PurchaseOrderModel> purchaseOrders(Pageable pageable) {
        Page<PurchaseOrder> orderPage = purchaseOrderRepositoryObjectFactory.getObject().findAll(pageable);

        return orderPage.map(toModel::convert);
    }

    @Override
    public Page<PurchaseOrderModel> purchaseOrders(Pageable pageable, String supplierPhone) {
        Page<PurchaseOrder> purchaseOrders = purchaseOrderRepositoryObjectFactory.getObject()
                .findPurchaseOrdersBySupplierPhoneNo(pageable, supplierPhone);

        return purchaseOrders.map(toModel::convert);
    }

    @Override
    public Optional<PurchaseOrderModel> purchaseOrder(String id) {
        if (purchaseOrderRepositoryObjectFactory.getObject().findById(id).isPresent()) {
            PurchaseOrderModel purchaseOrderModel = this.toModel.convert(purchaseOrderRepositoryObjectFactory.getObject().getById(id));

            return Optional.ofNullable(purchaseOrderModel);
        }

        return Optional.empty();
    }

    @Override
    public Optional<PurchaseOrderModel> addPurchaseOrder(PurchaseOrderModel purchaseOrderModel) {
        if (purchaseOrderModel.getStatus().equals(ProductPurchaseStatus.PENDING)
                || purchaseOrderModel.getStatus().equals(ProductPurchaseStatus.IN_STOCK)) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setProductName(purchaseOrderModel.getProductName());

            purchaseOrder.setCategory((productCategoryRepositoryObjectFactory.getObject().findById(purchaseOrderModel.getCategory()).isPresent())
                    ? productCategoryRepositoryObjectFactory.getObject().getById(purchaseOrderModel.getCategory()) : productCategoryRepositoryObjectFactory
                    .getObject().saveAndFlush(new ProductCategory(purchaseOrderModel.getCategory())));

            purchaseOrder.setSpecifications((purchaseOrderModel.getSpecifications() != null) ? purchaseOrderModel.getSpecifications() : null);
            purchaseOrder.setQuantity(purchaseOrderModel.getQuantity());
            purchaseOrder.setTotalPurchasePrice(purchaseOrderModel.getTotalPurchasePrice());
            purchaseOrder.setShippingCosts(purchaseOrderModel.getShippingCosts());
            purchaseOrder.setOtherCosts(purchaseOrderModel.getOtherCosts());
            purchaseOrder.setSellingPrice(purchaseOrderModel.getSellingPricePerUnit());

            if (supplierRepositoryObjectFactory.getObject().findById(purchaseOrderModel.getSupplier().getPhone()).isPresent()) {
                Supplier supplier = supplierRepositoryObjectFactory.getObject().getById(purchaseOrderModel.getSupplier().getPhone());
                purchaseOrder.setSupplier(supplier);
            } else {
                Supplier supplier = new Supplier();
                SupplierModel supplierModel = purchaseOrderModel.getSupplier();

                supplier.setName(supplierModel.getName());
                supplier.setPhoneNo(supplierModel.getPhone());
                supplier.setEmail((supplierModel.getEmail() != null) ? supplierModel.getEmail() : null);
                supplier.setAddress(supplierModel.getAddress());
                supplier.setWebsite((supplierModel.getWebsite() != null) ? supplierModel.getWebsite() : null);
                supplier.setNotes((supplierModel.getNotes() != null) ? supplierModel.getNotes() : null);
                supplier.setAddedBy(this.authenticationContext.getAuthenticatedUser());

                purchaseOrder.setSupplier(supplier);
            }

            purchaseOrder.setStatus(purchaseOrderModel.getStatus());
            purchaseOrder.setType(PurchaseOrderType.NEW_PRODUCT);
            purchaseOrder.setAddedBy(this.authenticationContext.getAuthenticatedUser());

            PurchaseOrder addedOrder = purchaseOrderRepositoryObjectFactory.getObject().saveAndFlush(purchaseOrder);

            if (addedOrder.getStatus().equals(ProductPurchaseStatus.IN_STOCK)) {
                Product product = new Product();
                product.setProductName(addedOrder.getProductName());
                product.setCategory(addedOrder.getCategory());
                product.setSpecifications(addedOrder.getSpecifications());
                product.setStock(addedOrder.getQuantity());
                product.setPrice(addedOrder.getSellingPrice());
                product.setPurchaseOrder(addedOrder);

                productRepositoryObjectFactory.getObject().saveAndFlush(product);
            }

            var today = LocalDateTime.now();
            FinanceRecord financeRecord = new FinanceRecord();
            financeRecord.setYear(today.getYear());
            financeRecord.setMonth(today.getMonthValue());
            financeRecord.setType(FinanceRecordType.EXPENSE);
            financeRecord.setValue(addedOrder.getTotalPurchasePrice() + addedOrder.getShippingCosts() + addedOrder.getOtherCosts());
            financeRecord.setPurchaseOrder(addedOrder);
            financeRepositoryObjectFactory.getObject().saveAndFlush(financeRecord);

            return Optional.ofNullable(this.toModel.convert(addedOrder));
        } else {
            throw new ResourceCrudException("Cancelled orders can not be added", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
        }
    }

    @Override
    public Optional<PurchaseOrderModel> updatePurchaseOrder(PurchaseOrderModel purchaseOrderModel, String productId) {
        if (purchaseOrderRepositoryObjectFactory.getObject().findById(purchaseOrderModel.getId()).isPresent()) {
            PurchaseOrder purchaseOrder = purchaseOrderRepositoryObjectFactory.getObject().getById(purchaseOrderModel.getId());

            if (purchaseOrder.getStatus().equals(ProductPurchaseStatus.PENDING)) {
                purchaseOrder.setSpecifications((purchaseOrderModel.getSpecifications() != null) ? purchaseOrderModel.getSpecifications() : null);
                purchaseOrder.setQuantity((purchaseOrderModel.getQuantity() != null) ? purchaseOrderModel.getQuantity() : purchaseOrder.getQuantity());
                purchaseOrder.setTotalPurchasePrice((purchaseOrderModel.getTotalPurchasePrice() != null) ?
                        purchaseOrderModel.getTotalPurchasePrice() : purchaseOrder.getTotalPurchasePrice());
                purchaseOrder.setShippingCosts((purchaseOrderModel.getShippingCosts() != null) ? purchaseOrderModel.getShippingCosts() : purchaseOrder.getShippingCosts());
                purchaseOrder.setOtherCosts((purchaseOrderModel.getOtherCosts() != null) ? purchaseOrderModel.getOtherCosts() : purchaseOrder.getOtherCosts());
                purchaseOrder.setSellingPrice((purchaseOrderModel.getSellingPricePerUnit() != null) ? purchaseOrderModel.getSellingPricePerUnit() : purchaseOrder.getSellingPrice());
                purchaseOrder.setStatus((purchaseOrderModel.getStatus() != null) ? purchaseOrderModel.getStatus() : purchaseOrder.getStatus());
                purchaseOrder.setUpdatedBy(this.authenticationContext.getAuthenticatedUser());

                FinanceRecord financeRecord = financeRepositoryObjectFactory.getObject().findAll()
                        .stream().filter(record -> record.getPurchaseOrder() != null && record.getPurchaseOrder().getId().equals(purchaseOrder.getId())
                                && record.getMonth() == purchaseOrder.getAddedOn().getMonthValue()
                                && record.getYear() == purchaseOrder.getAddedOn().getYear()
                                && record.getType().equals(FinanceRecordType.EXPENSE)).toList().get(0);
                financeRecord.setValue(purchaseOrder.getTotalPurchasePrice() + purchaseOrder.getShippingCosts() + purchaseOrder.getOtherCosts());

                if (purchaseOrderModel.getStatus().equals(ProductPurchaseStatus.IN_STOCK)) {
                    Product product;
                    if (productId != null && purchaseOrder.getType() == PurchaseOrderType.RESTOCK) {
                        if (productRepositoryObjectFactory.getObject().findById(productId).isPresent()
                                && purchaseOrder.getProductId().equals(productId)) {
                            product = productRepositoryObjectFactory.getObject().getById(productId);
                            product.setStock(product.getStock() + purchaseOrder.getQuantity());
                        } else {
                            throw new ResourceCrudException("Product not found with ID: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                        }
                    } else {
                        product = new Product();
                        product.setProductName(purchaseOrder.getProductName());
                        product.setCategory(purchaseOrder.getCategory());
                        product.setSpecifications((purchaseOrder.getSpecifications() != null) ? purchaseOrder.getSpecifications() : null);
                        product.setStock(purchaseOrder.getQuantity());
                    }
                    product.setPrice(purchaseOrder.getSellingPrice());
                    product.setPurchaseOrder(purchaseOrder);

                    purchaseOrderRepositoryObjectFactory.getObject().saveAndFlush(purchaseOrder);
                    productRepositoryObjectFactory.getObject().saveAndFlush(product);
                    financeRepositoryObjectFactory.getObject().saveAndFlush(financeRecord);
                } else if (purchaseOrderModel.getStatus().equals(ProductPurchaseStatus.CANCELLED)) {
                    FinanceRecord deletableRecord = financeRepositoryObjectFactory.getObject().findAll()
                            .stream().filter(record -> record.getPurchaseOrder().getId().equals(purchaseOrder.getId())
                                    && record.getMonth() == purchaseOrder.getAddedOn().getMonthValue()
                                    && record.getYear() == purchaseOrder.getAddedOn().getYear()
                                    && record.getType().equals(FinanceRecordType.EXPENSE)).toList().get(0);

                    financeRepositoryObjectFactory.getObject().delete(deletableRecord);
                } else {
                    purchaseOrderRepositoryObjectFactory.getObject().saveAndFlush(purchaseOrder);
                    financeRepositoryObjectFactory.getObject().saveAndFlush(financeRecord);
                }

                return purchaseOrderRepositoryObjectFactory.getObject().findById(purchaseOrder.getId()).map(toModel::convert);
            } else {
                throw new ResourceCrudException("Only Pending purchase orders can be updated",
                        HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<PurchaseOrderModel> createProductRestockRequest(PurchaseOrderModel purchaseOrderModel, String productId) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepositoryObjectFactory.getObject().findAll()
                .stream().filter((order) -> order.getProductId().equals(productId)
                        && order.getStatus() == ProductPurchaseStatus.PENDING).toList();

        if (purchaseOrders.size() == 0) {
            if (productRepositoryObjectFactory.getObject().findById(productId).isPresent()) {
                if (purchaseOrderModel.getStatus() != ProductPurchaseStatus.CANCELLED) {
                    Product product = productRepositoryObjectFactory.getObject().getById(productId);
                    PurchaseOrder purchaseOrder = new PurchaseOrder();
                    purchaseOrder.setProductName(product.getProductName());
                    purchaseOrder.setCategory(product.getCategory());
                    purchaseOrder.setSpecifications(product.getSpecifications());
                    purchaseOrder.setQuantity(purchaseOrderModel.getQuantity());
                    purchaseOrder.setTotalPurchasePrice(purchaseOrderModel.getTotalPurchasePrice());
                    purchaseOrder.setShippingCosts(purchaseOrderModel.getShippingCosts());
                    purchaseOrder.setOtherCosts(purchaseOrderModel.getOtherCosts());
                    purchaseOrder.setSellingPrice(purchaseOrderModel.getSellingPricePerUnit());

                    if (purchaseOrderModel.getSupplier().getPhone().equals(product.getPurchaseOrder().getSupplier().getPhoneNo())) {
                        purchaseOrder.setSupplier(product.getPurchaseOrder().getSupplier());
                    } else if (supplierRepositoryObjectFactory.getObject().findById(purchaseOrderModel.getSupplier().getPhone()).isPresent()
                            && !purchaseOrderModel.getSupplier().getPhone().equals(product.getPurchaseOrder().getSupplier().getPhoneNo())) {
                        Supplier supplier = supplierRepositoryObjectFactory.getObject().getById(purchaseOrderModel.getSupplier().getPhone());
                        purchaseOrder.setSupplier(supplier);
                    } else if (supplierRepositoryObjectFactory.getObject().findById(purchaseOrderModel.getSupplier().getPhone()).isEmpty()) {
                        Supplier supplier = new Supplier();
                        SupplierModel supplierModel = purchaseOrderModel.getSupplier();

                        supplier.setName(supplierModel.getName());
                        supplier.setPhoneNo(supplierModel.getPhone());
                        supplier.setEmail((supplierModel.getEmail() != null) ? supplierModel.getEmail() : null);
                        supplier.setAddress(supplierModel.getAddress());
                        supplier.setWebsite((supplierModel.getWebsite() != null) ? supplierModel.getWebsite() : null);
                        supplier.setNotes((supplierModel.getNotes() != null) ? supplierModel.getNotes() : null);
                        supplier.setAddedBy(this.authenticationContext.getAuthenticatedUser());
                        supplierRepositoryObjectFactory.getObject().saveAndFlush(supplier);

                        purchaseOrder.setSupplier(supplier);
                    }

                    purchaseOrder.setAddedBy(this.authenticationContext.getAuthenticatedUser());
                    purchaseOrder.setStatus(purchaseOrderModel.getStatus());
                    purchaseOrder.setType(PurchaseOrderType.RESTOCK);
                    purchaseOrder.setProductId(productId);
                    String id = purchaseOrderRepositoryObjectFactory.getObject().saveAndFlush(purchaseOrder).getId();

                    PurchaseOrder order = purchaseOrderRepositoryObjectFactory.getObject().getById(id);

                    if (purchaseOrderModel.getStatus() == ProductPurchaseStatus.IN_STOCK) {
                        FinanceRecord financeRecord = financeRepositoryObjectFactory.getObject().findAll()
                                .stream().filter(record -> record.getPurchaseOrder() != null && record.getPurchaseOrder().getId().equals(purchaseOrder.getId())
                                        && record.getMonth() == purchaseOrder.getAddedOn().getMonthValue()
                                        && record.getYear() == purchaseOrder.getAddedOn().getYear()
                                        && record.getType().equals(FinanceRecordType.EXPENSE)).toList().get(0);
                        financeRecord.setValue(purchaseOrder.getTotalPurchasePrice() + purchaseOrder.getShippingCosts() + purchaseOrder.getOtherCosts());
                        financeRepositoryObjectFactory.getObject().saveAndFlush(financeRecord);
                    }
                    return Optional.ofNullable(this.toModel.convert(order));
                } else {
                    throw new ResourceCrudException("Cancelled orders can not be added", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                }
            }

            throw new ResourceCrudException("Product not found with ID: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
        }

        throw new ResourceCrudException("An active restock request exists for product: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }
}
