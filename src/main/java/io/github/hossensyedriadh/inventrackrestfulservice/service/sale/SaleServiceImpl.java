package io.github.hossensyedriadh.inventrackrestfulservice.service.sale;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.*;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.FinanceRecordType;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.OrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PaymentStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.*;
import io.github.hossensyedriadh.inventrackrestfulservice.service.CurrentAuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SaleServiceImpl implements SaleService {
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final FinanceRepository financeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DeliveryMediumRepository deliveryMediumRepository;
    private final CurrentAuthenticationContext authenticationContext;
    private final HttpServletRequest httpServletRequest;
    private final ExecutorService executorService;

    @Autowired
    public SaleServiceImpl(SaleRepository saleRepository, SaleItemRepository saleItemRepository,
                           CustomerRepository customerRepository, ProductRepository productRepository,
                           ProductImageRepository productImageRepository, FinanceRepository financeRepository,
                           PaymentMethodRepository paymentMethodRepository, DeliveryMediumRepository deliveryMediumRepository,
                           CurrentAuthenticationContext authenticationContext, HttpServletRequest httpServletRequest) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.financeRepository = financeRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.deliveryMediumRepository = deliveryMediumRepository;
        this.authenticationContext = authenticationContext;
        this.httpServletRequest = httpServletRequest;

        this.executorService = Executors.newFixedThreadPool(3);
    }

    private List<SaleItem> getSaleItems(String saleId) {
        return saleItemRepository.findAll().stream()
                .filter(item -> item.getSale().getId().equals(saleId))
                .peek(item -> item.getProduct().setImages(this.getProductImages(item.getProduct().getId()))).toList();
    }

    private List<String> getProductImages(String productId) {
        return productImageRepository.findAll().stream()
                .filter(image -> image.getForProduct().getId().equals(productId))
                .map(ProductImage::getUrl).toList();
    }

    @Override
    public Page<Sale> saleOrders(Pageable pageable) {
        Page<Sale> salePage = saleRepository.findAll(pageable);
        return new PageImpl<>(salePage.stream().peek(sale ->
                sale.setProducts(this.getSaleItems(sale.getId()))).toList(), pageable,
                salePage.getTotalElements());
    }

    @Override
    public Page<Sale> saleOrders(Pageable pageable, String customerPhone) {
        Page<Sale> salePage = saleRepository.findSalesByCustomerPhoneNo(pageable, customerPhone);
        return new PageImpl<>(salePage.stream().peek(sale ->
                sale.setProducts(this.getSaleItems(sale.getId()))).toList(), pageable,
                salePage.getTotalElements());
    }

    @Override
    public Sale sale(String id) {
        if (saleRepository.findById(id).isPresent()) {
            Sale sale = saleRepository.findById(id).get();
            sale.setProducts(this.getSaleItems(sale.getId()));
            return sale;
        }

        throw new ResourceException("Sale not found with ID: " + id, HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public Sale add(Sale sale) {
        if (sale.getOrderStatus() != OrderStatus.CANCELLED) {
            if (customerRepository.findById(sale.getCustomer().getPhoneNo()).isEmpty()) {
                customerRepository.saveAndFlush(sale.getCustomer());
            }

            if (paymentMethodRepository.findById(sale.getPaymentMethod().getName()).isEmpty()) {
                paymentMethodRepository.saveAndFlush(sale.getPaymentMethod());
            }

            if (deliveryMediumRepository.findById(sale.getDeliveryMedium().getName()).isEmpty()) {
                deliveryMediumRepository.saveAndFlush(sale.getDeliveryMedium());
            }

            if (sale.getPaymentStatus() == PaymentStatus.PARTIAL) {
                if (sale.getTotalDue() >= sale.getTotalPayable()) {
                    throw new ResourceException("In-case of partial payment, total due must be less than total payable amount",
                            HttpStatus.BAD_REQUEST, httpServletRequest);
                }
            } else if (sale.getPaymentStatus() == PaymentStatus.COMPLETED) {
                if (sale.getTotalDue() != 0) {
                    throw new ResourceException("For complete payment, total due must be 0",
                            HttpStatus.BAD_REQUEST, httpServletRequest);
                }
            }

            sale.setAddedBy(this.authenticationContext.getAuthenticatedUser());

            Sale addedSale = saleRepository.saveAndFlush(sale);

            List<Product> soldProducts = new ArrayList<>();
            List<SaleItem> saleItems = sale.getProducts();
            saleItems.forEach(s -> s.setSale(addedSale));

            for (int i = 0; i < saleItems.size(); i += 1) {
                String currentProductId = sale.getProducts().get(i).getProduct().getId();

                if (productRepository.findById(currentProductId).isPresent()) {
                    Product product = productRepository.findById(currentProductId).get();
                    if (product.getStock() >= saleItems.get(i).getQuantity()) {
                        product.setStock(product.getStock() - saleItems.get(i).getQuantity());
                        soldProducts.add(product);
                    } else {
                        saleRepository.deleteById(addedSale.getId());
                        throw new ResourceException("Not enough stock for product ID: " + product.getId(),
                                HttpStatus.BAD_REQUEST, httpServletRequest);
                    }
                } else {
                    saleRepository.deleteById(addedSale.getId());
                    throw new ResourceException("Product not found with ID: " + currentProductId,
                            HttpStatus.BAD_REQUEST, httpServletRequest);
                }
            }

            saleItemRepository.saveAllAndFlush(saleItems);
            productRepository.saveAllAndFlush(soldProducts);

            this.executorService.submit(() -> {
                var today = LocalDateTime.now(ZoneId.systemDefault());
                FinanceRecord record = new FinanceRecord();
                record.setYear(today.getYear());
                record.setMonth(today.getMonthValue());
                record.setSale(addedSale);
                record.setType(FinanceRecordType.SALE);
                record.setValue(addedSale.getTotalPayable());
                financeRepository.saveAndFlush(record);
            });

            addedSale.setProducts(this.getSaleItems(addedSale.getId()));
            return addedSale;
        }

        throw new ResourceException("Cancelled orders can not be added", HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public Sale update(Sale sale) {
        if (saleRepository.findById(sale.getId()).isPresent()) {
            Sale existingSale = saleRepository.findById(sale.getId()).get();

            if (existingSale.getOrderStatus() == OrderStatus.PENDING || existingSale.getOrderStatus() == OrderStatus.CONFIRMED) {
                List<SaleItem> currentItems = saleItemRepository.findAll().stream()
                        .filter(item -> item.getSale().getId().equals(existingSale.getId())).toList();

                if (paymentMethodRepository.findById(sale.getPaymentMethod().getName()).isEmpty()) {
                    paymentMethodRepository.saveAndFlush(sale.getPaymentMethod());
                }

                if (deliveryMediumRepository.findById(sale.getDeliveryMedium().getName()).isEmpty()) {
                    deliveryMediumRepository.saveAndFlush(sale.getDeliveryMedium());
                }

                if (sale.getPaymentStatus() == PaymentStatus.PARTIAL) {
                    if (sale.getTotalDue() >= sale.getTotalPayable()) {
                        throw new ResourceException("In-case of partial payment, total due must be less than total payable amount",
                                HttpStatus.BAD_REQUEST, httpServletRequest);
                    }
                } else if (sale.getPaymentStatus() == PaymentStatus.COMPLETED) {
                    if (sale.getTotalDue() != 0) {
                        throw new ResourceException("For complete payment, total due must be 0",
                                HttpStatus.BAD_REQUEST, httpServletRequest);
                    }
                }

                this.executorService.submit(() -> {
                    List<FinanceRecord> financeRecords = financeRepository.findAll().stream()
                            .filter(record -> record.getSale() != null && record.getSale().getId().equals(existingSale.getId())
                                    && record.getMonth() == existingSale.getAddedOn().getMonthValue()
                                    && record.getYear() == existingSale.getAddedOn().getYear()
                                    && record.getType() == FinanceRecordType.SALE).toList();

                    if (!financeRecords.isEmpty()) {
                        FinanceRecord record = financeRecords.get(0);
                        record.setValue(sale.getTotalPayable());
                        financeRepository.saveAndFlush(record);
                    }
                });

                if (sale.getOrderStatus() == OrderStatus.CANCELLED) {
                    List<Product> soldProducts = new ArrayList<>(currentItems.size());

                    for (SaleItem item : currentItems) {
                        Product product = item.getProduct();
                        product.setStock(product.getStock() + item.getQuantity());
                        soldProducts.add(product);
                    }

                    productRepository.saveAllAndFlush(soldProducts);
                    saleItemRepository.deleteAll(currentItems);

                    this.executorService.submit(() -> {
                        List<FinanceRecord> financeRecords = financeRepository.findAll().stream()
                                .filter(record -> record.getSale() != null && record.getSale().getId().equals(existingSale.getId())
                                        && record.getMonth() == existingSale.getAddedOn().getMonthValue()
                                        && record.getYear() == existingSale.getAddedOn().getYear()
                                        && record.getType() == FinanceRecordType.SALE).toList();
                        financeRepository.deleteAll(financeRecords);
                    });
                } else {
                    if (sale.getProducts() != null) {
                        if (!sale.getProducts().equals(currentItems)) {
                            List<SaleItem> updatedSaleItems = new ArrayList<>();
                            List<Product> previousSoldProducts = new ArrayList<>();
                            List<Product> currentSoldProducts = new ArrayList<>();

                            for (SaleItem foundSaleItem : currentItems) {
                                Product previousSoldProduct = foundSaleItem.getProduct();
                                previousSoldProduct.setStock(previousSoldProduct.getStock() + foundSaleItem.getQuantity());
                                previousSoldProducts.add(previousSoldProduct);
                            }
                            productRepository.saveAllAndFlush(previousSoldProducts);
                            saleItemRepository.deleteAll(currentItems);

                            for (SaleItem updatedSaleItem : sale.getProducts()) {
                                String currentProduct = updatedSaleItem.getProduct().getId();
                                if (productRepository.findById(currentProduct).isPresent()) {
                                    Product currentSoldProduct = productRepository.findById(currentProduct).get();
                                    if (updatedSaleItem.getQuantity() <= currentSoldProduct.getStock()) {
                                        SaleItem saleItem = new SaleItem();
                                        saleItem.setProduct(currentSoldProduct);
                                        saleItem.setQuantity(updatedSaleItem.getQuantity());
                                        saleItem.setPrice(updatedSaleItem.getPrice());
                                        saleItem.setSale(existingSale);
                                        updatedSaleItems.add(saleItem);
                                        currentSoldProduct.setStock(currentSoldProduct.getStock() - updatedSaleItem.getQuantity());
                                        currentSoldProducts.add(currentSoldProduct);
                                    } else {
                                        throw new ResourceException("Not enough stock for product ID: " + currentSoldProduct.getId(),
                                                HttpStatus.BAD_REQUEST, httpServletRequest);
                                    }
                                } else {
                                    throw new ResourceException("Product not found with ID: " + currentProduct, HttpStatus.BAD_REQUEST,
                                            httpServletRequest);
                                }
                            }

                            productRepository.saveAllAndFlush(currentSoldProducts);
                            saleItemRepository.saveAllAndFlush(updatedSaleItems);

                            this.executorService.submit(() -> {
                                FinanceRecord financeRecord = financeRepository.findAll()
                                        .stream().filter(record -> record.getSale() != null && record.getSale().getId().equals(existingSale.getId())
                                                && record.getMonth() == existingSale.getAddedOn().getMonthValue()
                                                && record.getYear() == existingSale.getAddedOn().getYear()
                                                && record.getType().equals(FinanceRecordType.SALE)).toList().get(0);
                                financeRecord.setValue(sale.getTotalPayable());
                                financeRepository.saveAndFlush(financeRecord);
                            });
                        }
                    } else {
                        throw new ResourceException("Sale record must contain at-least 1 item", HttpStatus.BAD_REQUEST, httpServletRequest);
                    }
                }

                Sale addedSale = saleRepository.saveAndFlush(sale);
                addedSale.setProducts(this.getSaleItems(addedSale.getId()));

                return addedSale;
            } else {
                throw new ResourceException("Cancelled orders can not be updated", HttpStatus.BAD_REQUEST, httpServletRequest);
            }
        }

        throw new ResourceException("Sale order not found with ID: " + sale.getId(), HttpStatus.BAD_REQUEST, httpServletRequest);
    }
}
