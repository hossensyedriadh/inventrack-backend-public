package io.github.hossensyedriadh.InvenTrackRESTfulService.service.sale;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.*;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.FinanceRecordType;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.OrderStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.PaymentStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.SaleItemToModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.mapper.entityToModel.SaleToModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.CustomerModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.ProductModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SaleItemModel;
import io.github.hossensyedriadh.InvenTrackRESTfulService.model.SaleModel;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public final class SaleServiceImpl implements SaleService {
    private final ObjectFactory<SaleRepository> saleRepositoryObjectFactory;
    private final ObjectFactory<SaleItemRepository> saleItemRepositoryObjectFactory;
    private final ObjectFactory<CustomerRepository> customerRepositoryObjectFactory;
    private final ObjectFactory<ProductRepository> productRepositoryObjectFactory;
    private final ObjectFactory<ProductImageRepository> productImageRepositoryObjectFactory;
    private final ObjectFactory<FinanceRepository> financeRepositoryObjectFactory;
    private final ObjectFactory<PaymentMethodRepository> paymentMethodRepositoryObjectFactory;
    private final ObjectFactory<DeliveryMediumRepository> deliveryMediumRepositoryObjectFactory;
    private final SaleToModel toSaleModel;
    private final SaleItemToModel toSaleItemModel;
    private final CurrentAuthenticationContext authenticationContext;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public SaleServiceImpl(ObjectFactory<SaleRepository> saleRepositoryObjectFactory,
                           ObjectFactory<SaleItemRepository> saleItemRepositoryObjectFactory,
                           ObjectFactory<CustomerRepository> customerRepositoryObjectFactory,
                           ObjectFactory<ProductRepository> productRepositoryObjectFactory,
                           ObjectFactory<ProductImageRepository> productImageRepositoryObjectFactory,
                           ObjectFactory<FinanceRepository> financeRepositoryObjectFactory,
                           ObjectFactory<PaymentMethodRepository> paymentMethodRepositoryObjectFactory,
                           ObjectFactory<DeliveryMediumRepository> deliveryMediumRepositoryObjectFactory,
                           SaleToModel toSaleModel, SaleItemToModel toSaleItemModel,
                           CurrentAuthenticationContext authenticationContext,
                           HttpServletRequest httpServletRequest) {
        this.saleRepositoryObjectFactory = saleRepositoryObjectFactory;
        this.saleItemRepositoryObjectFactory = saleItemRepositoryObjectFactory;
        this.customerRepositoryObjectFactory = customerRepositoryObjectFactory;
        this.productRepositoryObjectFactory = productRepositoryObjectFactory;
        this.productImageRepositoryObjectFactory = productImageRepositoryObjectFactory;
        this.financeRepositoryObjectFactory = financeRepositoryObjectFactory;
        this.paymentMethodRepositoryObjectFactory = paymentMethodRepositoryObjectFactory;
        this.deliveryMediumRepositoryObjectFactory = deliveryMediumRepositoryObjectFactory;
        this.toSaleModel = toSaleModel;
        this.toSaleItemModel = toSaleItemModel;
        this.authenticationContext = authenticationContext;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public Page<SaleModel> getSales(Pageable pageable) {
        Page<Sale> salePage = saleRepositoryObjectFactory.getObject().findAll(pageable);
        Page<SaleModel> saleModelPage = salePage.map(toSaleModel::convert);

        for (int i = 0; i < saleModelPage.getContent().size(); i += 1) {
            String currentSaleId = saleModelPage.getContent().get(i).getId();
            saleModelPage.getContent().get(i).setProducts(this.getSaleItems(currentSaleId));
        }

        return saleModelPage;
    }

    @Override
    public Page<SaleModel> getSales(Pageable pageable, String customerPhone) {
        Page<Sale> salePage = saleRepositoryObjectFactory.getObject().findSalesByCustomerPhoneNo(pageable, customerPhone);
        Page<SaleModel> saleModelPage = salePage.map(toSaleModel::convert);

        for (int i = 0; i < saleModelPage.getContent().size(); i += 1) {
            String currentSaleId = saleModelPage.getContent().get(i).getId();
            saleModelPage.getContent().get(i).setProducts(this.getSaleItems(currentSaleId));
        }

        return saleModelPage;
    }

    @Override
    public Optional<SaleModel> getSale(String id) {
        if (saleRepositoryObjectFactory.getObject().findById(id).isPresent()) {
            SaleModel saleModel = this.toSaleModel.convert(saleRepositoryObjectFactory.getObject().getById(id));

            if (saleModel != null) {
                saleModel.setProducts(this.getSaleItems(id));
                return Optional.of(saleModel);
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public Optional<SaleModel> addSale(SaleModel saleModel) {
        if (saleModel.getOrderStatus() != OrderStatus.CANCELLED) {
            Sale sale = new Sale();

            CustomerModel customerModel = saleModel.getCustomer();
            Customer customer;

            if (customerRepositoryObjectFactory.getObject().findById(customerModel.getPhone()).isPresent()) {
                customer = customerRepositoryObjectFactory.getObject().getById(customerModel.getPhone());
            } else {
                customer = new Customer();
                customer.setName(customerModel.getName());
                customer.setPhoneNo(customerModel.getPhone());
                customer.setEmail(customerModel.getEmail());
                customer.setAddress(customerModel.getAddress());
                customerRepositoryObjectFactory.getObject().saveAndFlush(customer);
            }

            sale.setTotalPayable(saleModel.getTotalPayable());
            sale.setTotalDue(saleModel.getTotalDue());
            sale.setCustomer(customer);
            sale.setPaymentStatus(saleModel.getPaymentStatus());
            sale.setPaymentMethod((paymentMethodRepositoryObjectFactory.getObject().findById(saleModel.getPaymentMethod()).isPresent())
                    ? paymentMethodRepositoryObjectFactory.getObject().getById(saleModel.getPaymentMethod())
                    : paymentMethodRepositoryObjectFactory.getObject().saveAndFlush(new PaymentMethod(saleModel.getPaymentMethod())));
            sale.setPaymentDetails((saleModel.getPaymentDetails() != null) ? saleModel.getPaymentDetails() : null);
            sale.setOrderStatus(saleModel.getOrderStatus());
            sale.setDeliveryMedium((deliveryMediumRepositoryObjectFactory.getObject().findById(saleModel.getDeliveryMedium()).isPresent())
                    ? deliveryMediumRepositoryObjectFactory.getObject().getById(saleModel.getDeliveryMedium())
                    : deliveryMediumRepositoryObjectFactory.getObject().saveAndFlush(new DeliveryMedium(saleModel.getDeliveryMedium())));
            sale.setNotes((saleModel.getNotes() != null) ? saleModel.getNotes() : null);
            sale.setAddedBy(this.authenticationContext.getAuthenticatedUser());

            Sale addedSale;

            if (sale.getPaymentStatus() == PaymentStatus.PARTIAL) {
                if (sale.getTotalDue() < sale.getTotalPayable()) {
                    addedSale = saleRepositoryObjectFactory.getObject().saveAndFlush(sale);
                } else {
                    throw new ResourceCrudException("For partial payment, total due must be less than total payable amount",
                            HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                }
            } else if (sale.getPaymentStatus() == PaymentStatus.COMPLETED) {
                if (sale.getTotalDue() == 0) {
                    addedSale = saleRepositoryObjectFactory.getObject().saveAndFlush(sale);
                } else {
                    throw new ResourceCrudException("For complete payment, total due must be 0",
                            HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                }
            } else {
                addedSale = saleRepositoryObjectFactory.getObject().saveAndFlush(sale);
            }

            List<Product> soldProducts = new ArrayList<>();

            List<SaleItem> saleItems = new ArrayList<>(saleModel.getProducts().size());
            for (int i = 0; i < saleModel.getProducts().size(); i += 1) {
                SaleItem saleItem = new SaleItem();

                ProductModel productModel = saleModel.getProducts().get(i).getProduct();

                if (productRepositoryObjectFactory.getObject().findById(productModel.getId()).isPresent()) {
                    Product product = productRepositoryObjectFactory.getObject().getById(productModel.getId());
                    if (product.getStock() >= saleModel.getProducts().get(i).getQuantity()) {
                        saleItem.setProduct(product);
                        saleItem.setQuantity(saleModel.getProducts().get(i).getQuantity());
                        saleItem.setPrice(saleModel.getProducts().get(i).getPrice());
                        saleItem.setSale(addedSale);
                        product.setStock(product.getStock() - saleModel.getProducts().get(i).getQuantity());

                        saleItems.add(saleItem);
                        soldProducts.add(product);
                    } else {
                        saleRepositoryObjectFactory.getObject().deleteById(addedSale.getId());
                        throw new ResourceCrudException("Not enough stock for product id: " + product.getId(),
                                HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                    }
                } else {
                    saleRepositoryObjectFactory.getObject().deleteById(addedSale.getId());
                    throw new ResourceCrudException("Product not found with id: " + productModel.getId(),
                            HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                }
            }

            List<SaleItem> addedSaleItems = saleItemRepositoryObjectFactory.getObject().saveAllAndFlush(saleItems);
            productRepositoryObjectFactory.getObject().saveAllAndFlush(soldProducts);

            var today = LocalDateTime.now();
            FinanceRecord financeRecord = new FinanceRecord();
            financeRecord.setMonth(today.getMonthValue());
            financeRecord.setYear(today.getYear());
            financeRecord.setValue(saleModel.getTotalPayable());
            financeRecord.setType(FinanceRecordType.SALE);
            financeRecord.setSale(addedSale);
            financeRepositoryObjectFactory.getObject().saveAndFlush(financeRecord);

            SaleModel addedSaleModel = this.toSaleModel.convert(addedSale);
            if (addedSaleModel != null) {
                addedSaleModel.setProducts(addedSaleItems.stream().map(this.toSaleItemModel::convert).toList());
                return Optional.of(addedSaleModel);
            }

            return Optional.empty();
        }

        throw new ResourceCrudException("Cancelled orders should not be added", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    @Override
    public Optional<SaleModel> updateSale(SaleModel saleModel) {
        if (saleRepositoryObjectFactory.getObject().findById(saleModel.getId()).isPresent()) {
            SaleModel foundSaleModel = this.toSaleModel.convert(saleRepositoryObjectFactory.getObject().getById(saleModel.getId()));
            if (foundSaleModel != null) {
                if (foundSaleModel.getOrderStatus() == OrderStatus.PENDING || foundSaleModel.getOrderStatus() == OrderStatus.CONFIRMED) {
                    List<SaleItem> foundSaleItems = saleItemRepositoryObjectFactory.getObject().findAll()
                            .stream().filter(item -> item.getSale().getId().equals(foundSaleModel.getId())).toList();

                    List<SaleItemModel> foundSaleItemModels = foundSaleItems.stream().map(this.toSaleItemModel::convert).toList();

                    Sale sale = saleRepositoryObjectFactory.getObject().getById(saleModel.getId());
                    sale.setTotalPayable((saleModel.getTotalPayable() != null) ? saleModel.getTotalPayable() : sale.getTotalPayable());
                    sale.setTotalDue((saleModel.getTotalDue() != null) ? saleModel.getTotalDue() : sale.getTotalDue());
                    sale.setPaymentStatus((saleModel.getPaymentStatus() != null) ? saleModel.getPaymentStatus() : sale.getPaymentStatus());
                    sale.setPaymentMethod((saleModel.getPaymentMethod() != null && !saleModel.getPaymentMethod().equals(sale.getPaymentMethod().getName()))
                            ? (paymentMethodRepositoryObjectFactory.getObject().findById(saleModel.getPaymentMethod()).isPresent()
                            ? paymentMethodRepositoryObjectFactory.getObject().getById(saleModel.getPaymentMethod())
                            : paymentMethodRepositoryObjectFactory.getObject()
                            .saveAndFlush(new PaymentMethod(saleModel.getPaymentMethod()))) : sale.getPaymentMethod());
                    sale.setPaymentDetails((saleModel.getPaymentDetails() != null) ? saleModel.getPaymentDetails() : null);
                    sale.setOrderStatus((saleModel.getOrderStatus() != null) ? saleModel.getOrderStatus() : sale.getOrderStatus());
                    sale.setDeliveryMedium((saleModel.getDeliveryMedium() != null && !saleModel.getDeliveryMedium().equals(sale.getDeliveryMedium().getName()))
                            ? (deliveryMediumRepositoryObjectFactory.getObject().findById(saleModel.getDeliveryMedium()).isPresent()
                            ? deliveryMediumRepositoryObjectFactory.getObject().getById(saleModel.getDeliveryMedium())
                            : deliveryMediumRepositoryObjectFactory.getObject()
                            .saveAndFlush(new DeliveryMedium(saleModel.getDeliveryMedium()))) : sale.getDeliveryMedium());
                    sale.setNotes((saleModel.getNotes() != null) ? saleModel.getNotes() : null);
                    sale.setUpdatedBy(this.authenticationContext.getAuthenticatedUser());

                    if (sale.getPaymentStatus() == PaymentStatus.PARTIAL) {
                        if (sale.getTotalDue() < sale.getTotalPayable()) {
                            saleRepositoryObjectFactory.getObject().saveAndFlush(sale);
                        } else {
                            throw new ResourceCrudException("For partial payment, total due must be less than total payable amount",
                                    HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                        }
                    } else if (sale.getPaymentStatus() == PaymentStatus.COMPLETED) {
                        if (sale.getTotalDue() == 0) {
                            saleRepositoryObjectFactory.getObject().saveAndFlush(sale);
                        } else {
                            throw new ResourceCrudException("For complete payment, total due must be 0",
                                    HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                        }
                    } else {
                        saleRepositoryObjectFactory.getObject().saveAndFlush(sale);
                    }

                    if (sale.getOrderStatus() == OrderStatus.CANCELLED) {
                        List<SaleItem> saleItems = saleItemRepositoryObjectFactory.getObject().findAll()
                                .stream().filter(item -> item.getSale().getId().equals(sale.getId())).toList();

                        List<Product> products = new ArrayList<>();

                        for (SaleItem saleItem : saleItems) {
                            Product product = saleItem.getProduct();
                            product.setStock(product.getStock() + saleItem.getQuantity());
                            products.add(product);
                        }

                        FinanceRecord financeRecord = financeRepositoryObjectFactory.getObject().findAll()
                                .stream().filter(record -> record.getSale() != null && record.getSale().getId().equals(foundSaleModel.getId())
                                        && record.getMonth() == foundSaleModel.getAddedOn().getMonthValue()
                                        && record.getYear() == foundSaleModel.getAddedOn().getYear()
                                        && record.getType().equals(FinanceRecordType.SALE)).toList().get(0);
                        financeRepositoryObjectFactory.getObject().delete(financeRecord);

                        productRepositoryObjectFactory.getObject().saveAllAndFlush(products);

                        List<SaleItemModel> saleItemModels = saleItems.stream().map(this.toSaleItemModel::convert).toList();

                        SaleModel updatedSaleModel = this.toSaleModel.convert(this.saleRepositoryObjectFactory.getObject().getById(saleModel.getId()));
                        if (updatedSaleModel != null) {
                            updatedSaleModel.setProducts(saleItemModels);
                            return Optional.of(updatedSaleModel);
                        } else {
                            return Optional.empty();
                        }
                    } else {
                        if (saleModel.getProducts() != null) {
                            if (!saleModel.getProducts().equals(foundSaleItemModels)) {
                                List<SaleItem> updatedSaleItems = new ArrayList<>();
                                List<SaleItemModel> updatedSaleItemModels = saleModel.getProducts();
                                List<Product> previousSoldProducts = new ArrayList<>();
                                List<Product> currentSoldProducts = new ArrayList<>();

                                for (SaleItem foundSaleItem : foundSaleItems) {
                                    Product previousSoldProduct = foundSaleItem.getProduct();
                                    previousSoldProduct.setStock(previousSoldProduct.getStock() + foundSaleItem.getQuantity());
                                    previousSoldProducts.add(previousSoldProduct);
                                }
                                productRepositoryObjectFactory.getObject().saveAllAndFlush(previousSoldProducts);
                                saleItemRepositoryObjectFactory.getObject().deleteAll(foundSaleItems);

                                for (SaleItemModel updatedSaleItemModel : updatedSaleItemModels) {
                                    String currentProduct = updatedSaleItemModel.getProduct().getId();
                                    if (productRepositoryObjectFactory.getObject().findById(currentProduct).isPresent()) {
                                        Product currentSoldProduct = productRepositoryObjectFactory.getObject().getById(currentProduct);
                                        if (updatedSaleItemModel.getQuantity() <= currentSoldProduct.getStock()) {
                                            SaleItem saleItem = new SaleItem();
                                            saleItem.setProduct(currentSoldProduct);
                                            saleItem.setQuantity(updatedSaleItemModel.getQuantity());
                                            saleItem.setPrice(updatedSaleItemModel.getPrice());
                                            saleItem.setSale(saleRepositoryObjectFactory.getObject().getById(saleModel.getId()));
                                            updatedSaleItems.add(saleItem);
                                            currentSoldProduct.setStock(currentSoldProduct.getStock() - updatedSaleItemModel.getQuantity());
                                            currentSoldProducts.add(currentSoldProduct);
                                        } else {
                                            throw new ResourceCrudException("Not enough stock for product id: " + currentSoldProduct.getId(),
                                                    HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                                        }
                                    } else {
                                        throw new ResourceCrudException("Product not found with id: " + currentProduct,
                                                HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                                    }
                                }
                                productRepositoryObjectFactory.getObject().saveAllAndFlush(currentSoldProducts);
                                saleItemRepositoryObjectFactory.getObject().saveAllAndFlush(updatedSaleItems);

                                List<SaleItem> saleItems = saleItemRepositoryObjectFactory.getObject().findAll()
                                        .stream().filter(item -> item.getSale().getId().equals(saleModel.getId())).toList();

                                List<SaleItemModel> saleItemModels = saleItems.stream().map(this.toSaleItemModel::convert).toList();

                                FinanceRecord financeRecord = financeRepositoryObjectFactory.getObject().findAll()
                                        .stream().filter(record -> record.getSale() != null && record.getSale().getId().equals(foundSaleModel.getId())
                                                && record.getMonth() == foundSaleModel.getAddedOn().getMonthValue()
                                                && record.getYear() == foundSaleModel.getAddedOn().getYear()
                                                && record.getType().equals(FinanceRecordType.SALE)).toList().get(0);
                                financeRecord.setValue(sale.getTotalPayable());

                                financeRepositoryObjectFactory.getObject().saveAndFlush(financeRecord);

                                SaleModel updatedSaleModel = this.toSaleModel.convert(this.saleRepositoryObjectFactory.getObject().getById(saleModel.getId()));
                                if (updatedSaleModel != null) {
                                    updatedSaleModel.setProducts(saleItemModels);
                                    return Optional.of(updatedSaleModel);
                                } else {
                                    return Optional.empty();
                                }
                            }
                        } else {
                            return Optional.ofNullable(this.toSaleModel.convert(saleRepositoryObjectFactory.getObject().getById(sale.getId())));
                        }
                    }
                } else {
                    throw new ResourceCrudException("Cancelled Sale records can not be updated", HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private List<SaleItemModel> getSaleItems(String saleId) {
        List<SaleItem> saleItems = saleItemRepositoryObjectFactory.getObject().findAll()
                .stream().filter(item -> item.getSale().getId().equals(saleId)).toList();

        List<SaleItemModel> saleItemModels = saleItems.stream().map(this.toSaleItemModel::convert).toList();
        List<SaleItemModel> models = new ArrayList<>(saleItemModels.size());

        for (SaleItemModel saleItemModel : saleItemModels) {
            ProductModel product = saleItemModel.getProduct();
            product.setImages(this.getProductImages(saleItemModel.getProduct().getId()));
            saleItemModel.setProduct(product);
            models.add(saleItemModel);
        }

        return models;
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
}
