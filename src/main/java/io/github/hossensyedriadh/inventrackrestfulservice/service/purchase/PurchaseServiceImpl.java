package io.github.hossensyedriadh.inventrackrestfulservice.service.purchase;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.FinanceRecord;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.Product;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.ProductCategory;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.PurchaseOrder;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.FinanceRecordType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PurchaseOrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PurchaseOrderType;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.*;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final FinanceRepository financeRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CurrentAuthenticationContext authenticationContext;
    private final HttpServletRequest httpServletRequest;
    private final ExecutorService executorService;

    @Autowired
    public PurchaseServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                               SupplierRepository supplierRepository, ProductRepository productRepository,
                               FinanceRepository financeRepository, ProductCategoryRepository productCategoryRepository,
                               CurrentAuthenticationContext authenticationContext, HttpServletRequest httpServletRequest) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.financeRepository = financeRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.authenticationContext = authenticationContext;
        this.httpServletRequest = httpServletRequest;

        this.executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public Page<PurchaseOrder> purchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable);
    }

    @Override
    public Page<PurchaseOrder> purchaseOrders(Pageable pageable, String supplierPhone) {
        return purchaseOrderRepository.findPurchaseOrdersBySupplierPhoneNo(pageable, supplierPhone);
    }

    @Override
    public PurchaseOrder purchaseOrder(String id) {
        if (purchaseOrderRepository.findById(id).isPresent()) {
            return purchaseOrderRepository.findById(id).get();
        }

        throw new ResourceException("Purchase order not found with ID: " + id, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public PurchaseOrder addPurchaseOrder(PurchaseOrder purchaseOrder) {
        if (purchaseOrder.getStatus().equals(PurchaseOrderStatus.PENDING)
                || purchaseOrder.getStatus().equals(PurchaseOrderStatus.IN_STOCK)) {
            if (productCategoryRepository.findById(purchaseOrder.getCategory().getName()).isEmpty()) {
                ProductCategory category = new ProductCategory();
                category.setName(purchaseOrder.getCategory().getName());
                productCategoryRepository.saveAndFlush(category);
            }

            if (supplierRepository.findById(purchaseOrder.getSupplier().getPhoneNo()).isEmpty()) {
                supplierRepository.saveAndFlush(purchaseOrder.getSupplier());
            }

            purchaseOrder.setOrderType(PurchaseOrderType.NEW_PRODUCT);
            purchaseOrder.setAddedBy(this.authenticationContext.getAuthenticatedUser());

            PurchaseOrder addedOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

            if (addedOrder.getStatus().equals(PurchaseOrderStatus.IN_STOCK)) {
                this.executorService.submit(() -> {
                    Product product = new Product();
                    product.setProductName(addedOrder.getProductName());
                    product.setCategory(addedOrder.getCategory());
                    product.setSpecifications(addedOrder.getSpecifications());
                    product.setStock(addedOrder.getQuantity());
                    product.setPrice(addedOrder.getSellingPricePerUnit());
                    product.setPurchaseOrder(addedOrder);

                    productRepository.saveAndFlush(product);
                });
            }

            this.executorService.submit(() -> {
                var today = LocalDateTime.now(ZoneId.systemDefault());
                FinanceRecord purchaseRecord = new FinanceRecord();
                purchaseRecord.setYear(today.getYear());
                purchaseRecord.setMonth(today.getMonthValue());
                purchaseRecord.setType(FinanceRecordType.EXPENSE);
                purchaseRecord.setValue(addedOrder.getTotalPurchasePrice() + addedOrder.getShippingCosts() + addedOrder.getOtherCosts());
                purchaseRecord.setPurchaseOrder(addedOrder);
                financeRepository.saveAndFlush(purchaseRecord);
            });

            return addedOrder;
        } else {
            throw new ResourceException("Cancelled orders can not be added", HttpStatus.BAD_REQUEST, httpServletRequest);
        }
    }

    @Override
    public PurchaseOrder updatePurchaseOrder(PurchaseOrder purchaseOrder, @Nullable String productId) {
        if (purchaseOrderRepository.findById(purchaseOrder.getId()).isPresent()) {
            PurchaseOrder currentOrder = purchaseOrderRepository.findById(purchaseOrder.getId()).get();

            if (currentOrder.getStatus().equals(PurchaseOrderStatus.PENDING)) {
                PurchaseOrder addedOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

                List<FinanceRecord> financeRecords = financeRepository.findAll().stream()
                        .filter(record -> record.getPurchaseOrder() != null
                                && record.getPurchaseOrder().getId().equals(purchaseOrder.getId())
                                && record.getMonth() == purchaseOrder.getAddedOn().getMonthValue()
                                && record.getYear() == purchaseOrder.getAddedOn().getYear()
                                && record.getType().equals(FinanceRecordType.EXPENSE)).toList();

                if (!financeRecords.isEmpty()) {
                    this.executorService.submit(() -> {
                        FinanceRecord record = financeRecords.get(0);
                        record.setValue(purchaseOrder.getTotalPurchasePrice() + purchaseOrder.getShippingCosts() + purchaseOrder.getOtherCosts());
                        financeRepository.saveAndFlush(record);
                    });
                }

                if (purchaseOrder.getStatus().equals(PurchaseOrderStatus.IN_STOCK)) {
                    Product product;

                    if (productId != null && purchaseOrder.getOrderType().equals(PurchaseOrderType.RESTOCK)) {
                        if (productRepository.findById(productId).isPresent()) {
                            product = productRepository.findById(productId).get();
                            product.setStock(product.getStock() + purchaseOrder.getQuantity());
                            product.setSpecifications(purchaseOrder.getSpecifications());
                            product.setPurchaseOrder(addedOrder);
                        } else {
                            throw new ResourceException("Product not found with ID: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest);
                        }
                    } else {
                        product = new Product();
                        product.setProductName(purchaseOrder.getProductName());
                        product.setCategory(purchaseOrder.getCategory());
                        product.setSpecifications(purchaseOrder.getSpecifications());
                        product.setStock(purchaseOrder.getQuantity());
                        product.setPurchaseOrder(purchaseOrder);
                    }
                    product.setPrice(purchaseOrder.getSellingPricePerUnit());
                    productRepository.saveAndFlush(product);
                } else if (purchaseOrder.getStatus().equals(PurchaseOrderStatus.CANCELLED)) {
                    this.executorService.submit(() -> {
                        List<FinanceRecord> records = financeRepository.findAll().stream()
                                .filter(record -> record.getPurchaseOrder().getId().equals(purchaseOrder.getId())
                                        && record.getMonth() == purchaseOrder.getAddedOn().getMonthValue()
                                        && record.getYear() == purchaseOrder.getAddedOn().getYear()
                                        && record.getType().equals(FinanceRecordType.EXPENSE)).toList();
                        financeRepository.deleteAll(records);
                    });
                }

                return purchaseOrder;
            } else {
                throw new ResourceException("Only pending purchase orders can be updated", HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        } else {
            throw new ResourceException("Purchase order not found with ID: " + purchaseOrder.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
        }
    }

    @Override
    public PurchaseOrder createProductRestockOrder(PurchaseOrder purchaseOrder, String productId) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll().stream()
                .filter(order -> {
                    if (order.getProductId() != null) {
                        return order.getOrderType() == PurchaseOrderType.RESTOCK &&
                                order.getProductId().equals(productId) && order.getStatus() == PurchaseOrderStatus.PENDING;
                    }
                    return false;
                }).toList();

        if (purchaseOrders.isEmpty()) {
            if (productRepository.findById(productId).isPresent()) {
                if (purchaseOrder.getStatus() != PurchaseOrderStatus.CANCELLED) {
                    Product product = productRepository.findById(productId).get();

                    purchaseOrder.setProductName(product.getProductName());
                    purchaseOrder.setCategory(product.getCategory());
                    if (supplierRepository.findById(purchaseOrder.getSupplier().getPhoneNo()).isEmpty()) {
                        supplierRepository.saveAndFlush(purchaseOrder.getSupplier());
                    }
                    purchaseOrder.setAddedBy(this.authenticationContext.getAuthenticatedUser());
                    purchaseOrder.setOrderType(PurchaseOrderType.RESTOCK);
                    purchaseOrder.setProductId(productId);
                    PurchaseOrder addedOrder = purchaseOrderRepository.saveAndFlush(purchaseOrder);

                    if (purchaseOrder.getStatus() == PurchaseOrderStatus.IN_STOCK) {
                        product.setStock(product.getStock() + purchaseOrder.getQuantity());
                        product.setPrice(purchaseOrder.getSellingPricePerUnit());
                        product.setSpecifications(purchaseOrder.getSpecifications());
                        product.setPurchaseOrder(addedOrder);
                    }

                    this.executorService.submit(() -> {
                        var today = LocalDateTime.now(ZoneId.systemDefault());
                        FinanceRecord financeRecord = new FinanceRecord();
                        financeRecord.setMonth(today.getMonthValue());
                        financeRecord.setYear(today.getYear());
                        financeRecord.setType(FinanceRecordType.EXPENSE);
                        financeRecord.setValue(addedOrder.getTotalPurchasePrice() + addedOrder.getShippingCosts() + addedOrder.getOtherCosts());
                        financeRecord.setPurchaseOrder(addedOrder);
                        financeRepository.saveAndFlush(financeRecord);
                    });

                    return addedOrder;
                } else {
                    throw new ResourceException("Cancelled orders can not be added", HttpStatus.BAD_REQUEST, httpServletRequest);
                }
            } else {
                throw new ResourceException("Product not found with ID: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        } else {
            throw new ResourceException("An active restock request exists for product: " + productId, HttpStatus.BAD_REQUEST, httpServletRequest);
        }
    }
}
