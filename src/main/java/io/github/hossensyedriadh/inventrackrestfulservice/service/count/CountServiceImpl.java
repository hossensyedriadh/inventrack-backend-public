package io.github.hossensyedriadh.inventrackrestfulservice.service.count;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.Sale;
import io.github.hossensyedriadh.inventrackrestfulservice.entity.SaleItem;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.OrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.PurchaseOrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProductRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.PurchaseOrderRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SaleItemRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountServiceImpl implements CountService {
    private final ProductRepository productRepository;

    private final SaleRepository saleRepository;

    private final SaleItemRepository saleItemRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public CountServiceImpl(ProductRepository productRepository, SaleRepository saleRepository,
                            SaleItemRepository saleItemRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public Double totalSales() {
        return saleRepository.findAll().stream()
                .filter(sale -> sale.getOrderStatus().equals(OrderStatus.CONFIRMED))
                .mapToDouble(Sale::getTotalPayable).sum();
    }

    @Override
    public Double totalCost() {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getStatus() != PurchaseOrderStatus.CANCELLED).mapToDouble(order -> order.getTotalPurchasePrice()
                        + order.getShippingCosts() + order.getOtherCosts()).sum();
    }

    @Override
    public Integer unitsSold() {
        return saleItemRepository.findAll().stream()
                .filter(item -> item.getSale().getOrderStatus().equals(OrderStatus.CONFIRMED))
                .mapToInt(SaleItem::getQuantity).sum();
    }

    @Override
    public Double stockAvailable() {
        return productRepository.findAll().stream()
                .mapToDouble(product -> product.getStock() * product.getPrice()).sum();
    }
}
