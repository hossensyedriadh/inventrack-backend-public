package io.github.hossensyedriadh.InvenTrackRESTfulService.service.misc;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.DeliveryMedium;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.PaymentMethod;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.ProductCategory;

import java.util.List;
import java.util.Optional;

public sealed interface MiscService permits MiscServiceImpl {
    List<ProductCategory> getProductCategories();

    Optional<ProductCategory> addProductCategory(ProductCategory category);

    List<PaymentMethod> getPaymentMethods();

    Optional<PaymentMethod> addPaymentMethod(PaymentMethod method);

    List<DeliveryMedium> getDeliveryMediums();

    Optional<DeliveryMedium> addDeliveryMedium(DeliveryMedium medium);
}