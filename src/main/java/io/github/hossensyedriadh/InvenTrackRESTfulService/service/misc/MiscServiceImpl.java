package io.github.hossensyedriadh.InvenTrackRESTfulService.service.misc;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.DeliveryMedium;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.PaymentMethod;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.ProductCategory;
import io.github.hossensyedriadh.InvenTrackRESTfulService.exception.ResourceCrudException;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.DeliveryMediumRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.PaymentMethodRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProductCategoryRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
public final class MiscServiceImpl implements MiscService {
    private final ObjectFactory<ProductCategoryRepository> productCategoryRepositoryObjectFactory;
    private final ObjectFactory<PaymentMethodRepository> paymentMethodRepositoryObjectFactory;
    private final ObjectFactory<DeliveryMediumRepository> deliveryMediumRepositoryObjectFactory;
    private final HttpServletRequest httpServletRequest;

    @Autowired
    public MiscServiceImpl(ObjectFactory<ProductCategoryRepository> productCategoryRepositoryObjectFactory,
                           ObjectFactory<PaymentMethodRepository> paymentMethodRepositoryObjectFactory,
                           ObjectFactory<DeliveryMediumRepository> deliveryMediumRepositoryObjectFactory,
                           HttpServletRequest httpServletRequest) {
        this.productCategoryRepositoryObjectFactory = productCategoryRepositoryObjectFactory;
        this.paymentMethodRepositoryObjectFactory = paymentMethodRepositoryObjectFactory;
        this.deliveryMediumRepositoryObjectFactory = deliveryMediumRepositoryObjectFactory;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public List<ProductCategory> getProductCategories() {
        return productCategoryRepositoryObjectFactory.getObject().findAll();
    }

    @Override
    public Optional<ProductCategory> addProductCategory(ProductCategory category) {
        if (!productCategoryRepositoryObjectFactory.getObject().existsById(category.getName())) {
            return Optional.of(productCategoryRepositoryObjectFactory.getObject().saveAndFlush(category));
        }

        throw new ResourceCrudException("Product category " + category.getName() + " already exists",
                HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    @Override
    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethodRepositoryObjectFactory.getObject().findAll();
    }

    @Override
    public Optional<PaymentMethod> addPaymentMethod(PaymentMethod method) {
        if (!paymentMethodRepositoryObjectFactory.getObject().existsById(method.getName())) {
            return Optional.of(paymentMethodRepositoryObjectFactory.getObject().saveAndFlush(method));
        }

        throw new ResourceCrudException("Payment method " + method.getName() + " already exists",
                HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }

    @Override
    public List<DeliveryMedium> getDeliveryMediums() {
        return deliveryMediumRepositoryObjectFactory.getObject().findAll();
    }

    @Override
    public Optional<DeliveryMedium> addDeliveryMedium(DeliveryMedium medium) {
        if (!deliveryMediumRepositoryObjectFactory.getObject().existsById(medium.getName())) {
            return Optional.of(deliveryMediumRepositoryObjectFactory.getObject().saveAndFlush(medium));
        }

        throw new ResourceCrudException("Delivery medium " + medium.getName() + " already exists",
                HttpStatus.BAD_REQUEST, httpServletRequest.getRequestURI());
    }
}
