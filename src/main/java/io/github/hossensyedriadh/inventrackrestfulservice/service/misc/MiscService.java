package io.github.hossensyedriadh.inventrackrestfulservice.service.misc;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.DeliveryMedium;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.PaymentMethod;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.ProductCategory;

import java.util.List;

public interface MiscService {
    List<ProductCategory> getProductCategories();

    ProductCategory addProductCategory(ProductCategory category);

    List<PaymentMethod> getPaymentMethods();

    PaymentMethod addPaymentMethod(PaymentMethod paymentMethod);

    List<DeliveryMedium> getDeliveryMediums();

    DeliveryMedium addDeliveryMedium(DeliveryMedium deliveryMedium);
}
