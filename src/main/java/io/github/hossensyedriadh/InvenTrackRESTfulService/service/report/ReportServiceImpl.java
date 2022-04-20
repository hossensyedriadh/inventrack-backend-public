package io.github.hossensyedriadh.InvenTrackRESTfulService.service.report;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.PurchaseOrder;
import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.Sale;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.OrderStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.ProductPurchaseStatus;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.PurchaseOrderRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SaleItemRepository;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.SaleRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public final class ReportServiceImpl implements ReportService {
    private final ObjectFactory<PurchaseOrderRepository> purchaseOrderRepositoryObjectFactory;
    private final ObjectFactory<SaleRepository> saleRepositoryObjectFactory;
    private final ObjectFactory<SaleItemRepository> saleItemRepositoryObjectFactory;

    @Autowired
    public ReportServiceImpl(ObjectFactory<PurchaseOrderRepository> purchaseOrderRepositoryObjectFactory,
                             ObjectFactory<SaleRepository> saleRepositoryObjectFactory,
                             ObjectFactory<SaleItemRepository> saleItemRepositoryObjectFactory) {
        this.purchaseOrderRepositoryObjectFactory = purchaseOrderRepositoryObjectFactory;
        this.saleRepositoryObjectFactory = saleRepositoryObjectFactory;
        this.saleItemRepositoryObjectFactory = saleItemRepositoryObjectFactory;
    }

    private List<Integer> getSaleMonths(int year) {
        List<Sale> sales = saleRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getOrderStatus().equals(OrderStatus.CONFIRMED)
                        && record.getAddedOn().getYear() == year).toList();

        List<Integer> months = new ArrayList<>();
        for (Sale sale : sales) {
            months.add(sale.getAddedOn().getMonthValue());
        }

        return months;
    }

    private List<Integer> getPurchaseMonths(int year) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getStatus().equals(ProductPurchaseStatus.PENDING)
                        || record.getStatus().equals(ProductPurchaseStatus.IN_STOCK)
                        && record.getAddedOn().getYear() == year).toList();

        List<Integer> months = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            months.add(purchaseOrder.getAddedOn().getMonthValue());
        }

        return months;
    }

    @Override
    public Map<Integer, Integer> getUnitsSold() {
        List<Sale> sales = saleRepositoryObjectFactory.getObject().findAll();

        List<Integer> years = new ArrayList<>();
        for (Sale sale : sales) {
            years.add(sale.getAddedOn().getYear());
        }
        years = years.stream().distinct().toList();

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer year : years) {
            data.put(year, saleItemRepositoryObjectFactory.getObject().getUnitsSoldByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> getUnitsSold(int year) {
        List<Integer> months = this.getSaleMonths(year);

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer month : months) {
            data.put(month, saleItemRepositoryObjectFactory.getObject().getUnitsSoldByMonth(month));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> getPurchaseOrderCount() {
        List<Integer> years = purchaseOrderRepositoryObjectFactory.getObject().findAll()
                .stream().map(order -> order.getAddedOn().getYear()).distinct().toList();

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer year : years) {
            data.put(year, purchaseOrderRepositoryObjectFactory.getObject().getPurchaseOrderCountByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> getPurchaseOrderCount(int year) {
        List<Integer> months = this.getPurchaseMonths(year);

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer month : months) {
            data.put(month, purchaseOrderRepositoryObjectFactory.getObject().getPurchaseOrderCountByMonth(month));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> getSaleOrderCount() {
        List<Integer> years = saleRepositoryObjectFactory.getObject().findAll()
                .stream().map(order -> order.getAddedOn().getYear()).distinct().toList();

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer year : years) {
            data.put(year, saleRepositoryObjectFactory.getObject().getSaleOrderCountByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> getSaleOrderCount(int year) {
        List<Integer> months = this.getSaleMonths(year);

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer month : months) {
            data.put(month, saleRepositoryObjectFactory.getObject().getSaleOrderCountByMonth(month));
        }

        return data;
    }
}
