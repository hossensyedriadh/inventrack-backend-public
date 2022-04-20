package io.github.hossensyedriadh.InvenTrackRESTfulService.service.count;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Sale;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.SaleItem;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.OrderStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.ProductPurchaseStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.ProductRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.PurchaseOrderRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SaleItemRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SaleRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class CountServiceImpl implements CountService {
    private final ObjectFactory<ProductRepository> productRepositoryObjectFactory;

    private final ObjectFactory<SaleRepository> saleRepositoryObjectFactory;

    private final ObjectFactory<SaleItemRepository> saleItemRepositoryObjectFactory;

    private final ObjectFactory<PurchaseOrderRepository> purchaseOrderRepositoryObjectFactory;

    @Autowired
    public CountServiceImpl(ObjectFactory<ProductRepository> productRepositoryObjectFactory,
                            ObjectFactory<SaleRepository> saleRepositoryObjectFactory,
                            ObjectFactory<SaleItemRepository> saleItemRepositoryObjectFactory,
                            ObjectFactory<PurchaseOrderRepository> purchaseOrderRepositoryObjectFactory) {
        this.productRepositoryObjectFactory = productRepositoryObjectFactory;
        this.saleRepositoryObjectFactory = saleRepositoryObjectFactory;
        this.saleItemRepositoryObjectFactory = saleItemRepositoryObjectFactory;
        this.purchaseOrderRepositoryObjectFactory = purchaseOrderRepositoryObjectFactory;
    }

    @Override
    public Double getTotalSales() {
        return saleRepositoryObjectFactory.getObject().findAll().stream()
                .filter(sale -> sale.getOrderStatus().equals(OrderStatus.CONFIRMED)).mapToDouble(Sale::getTotalPayable).sum();
    }

    @Override
    public Double getTotalCost() {
        return purchaseOrderRepositoryObjectFactory.getObject().findAll().stream()
                .filter(order -> order.getStatus().equals(ProductPurchaseStatus.IN_STOCK) || order.getStatus().equals(ProductPurchaseStatus.PENDING))
                .mapToDouble(order -> order.getTotalPurchasePrice() + order.getShippingCosts() + order.getOtherCosts()).sum();
    }

    @Override
    public Integer getProductsSold() {
        return saleItemRepositoryObjectFactory.getObject().findAll().stream()
                .filter(sale -> sale.getSale().getOrderStatus().equals(OrderStatus.CONFIRMED)).mapToInt(SaleItem::getQuantity).sum();
    }

    @Override
    public Double getStockAvailable() {
        return productRepositoryObjectFactory.getObject().findAll().stream()
                .mapToDouble((product) -> product.getStock() * product.getPrice()).sum();
    }
}
