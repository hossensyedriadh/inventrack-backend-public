package io.github.hossensyedriadh.inventrackrestfulservice.service.misc;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.DeliveryMedium;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.PaymentMethod;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.ProductCategory;
import io.github.hossensyedriadh.inventrackrestfulservice.exception.ResourceException;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.DeliveryMediumRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.PaymentMethodRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MiscServiceImpl implements MiscService {
    private final ProductCategoryRepository productCategoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final DeliveryMediumRepository deliveryMediumRepository;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public MiscServiceImpl(ProductCategoryRepository productCategoryRepository, PaymentMethodRepository paymentMethodRepository,
                           DeliveryMediumRepository deliveryMediumRepository, HttpServletRequest httpServletRequest) {
        this.productCategoryRepository = productCategoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.deliveryMediumRepository = deliveryMediumRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public List<ProductCategory> getProductCategories() {
        return productCategoryRepository.findAll();
    }

    @Override
    public ProductCategory addProductCategory(ProductCategory category) {
        if (productCategoryRepository.findById(category.getName()).isEmpty()) {
            return productCategoryRepository.saveAndFlush(category);
        }

        throw new ResourceException("Product category " + category.getName() + " already exists",
                HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethodRepository.findAll();
    }

    @Override
    public PaymentMethod addPaymentMethod(PaymentMethod paymentMethod) {
        if (paymentMethodRepository.findById(paymentMethod.getName()).isEmpty()) {
            return paymentMethodRepository.saveAndFlush(paymentMethod);
        }

        throw new ResourceException("Payment method " + paymentMethod.getName() + " already exists",
                HttpStatus.BAD_REQUEST, httpServletRequest);
    }

    @Override
    public List<DeliveryMedium> getDeliveryMediums() {
        return deliveryMediumRepository.findAll();
    }

    @Override
    public DeliveryMedium addDeliveryMedium(DeliveryMedium deliveryMedium) {
        if (deliveryMediumRepository.findById(deliveryMedium.getName()).isEmpty()) {
            return deliveryMediumRepository.saveAndFlush(deliveryMedium);
        }

        throw new ResourceException("Delivery medium " + deliveryMedium.getName() + " already exists",
                HttpStatus.BAD_REQUEST, httpServletRequest);
    }
}
