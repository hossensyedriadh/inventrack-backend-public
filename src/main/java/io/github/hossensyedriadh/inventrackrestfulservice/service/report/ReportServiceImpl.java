package io.github.hossensyedriadh.inventrackrestfulservice.service.report;

import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.OrderStatus;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.PurchaseOrderRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SaleItemRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    @Autowired
    public ReportServiceImpl(PurchaseOrderRepository purchaseOrderRepository, SaleRepository saleRepository,
                             SaleItemRepository saleItemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
    }

    private List<Integer> getSaleMonths(int year) {
        return saleRepository.findAll().stream()
                .filter(record -> record.getOrderStatus().equals(OrderStatus.CONFIRMED)
                        && record.getAddedOn().getYear() == year)
                .map(record -> record.getAddedOn().getMonthValue()).distinct().toList();
    }

    private List<Integer> getPurchaseMonths(int year) {
        return purchaseOrderRepository.findAll().stream()
                .filter(record -> record.getAddedOn().getYear() == year)
                .map(record -> record.getAddedOn().getMonthValue()).distinct().toList();
    }

    @Override
    public Map<Integer, Integer> unitsSold() {
        List<Integer> years = saleRepository.findAll().stream()
                .map(record -> record.getAddedOn().getYear()).distinct().toList();

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer year : years) {
            data.put(year, saleItemRepository.getUnitsSold(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> unitsSold(int year) {
        List<Integer> months = this.getSaleMonths(year);

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer month : months) {
            data.put(month, saleItemRepository.getUnitsSold(year, month));
        }

        return data;
    }

    @Override
    public Integer unitsSold(int year, int month) {
        return saleItemRepository.getUnitsSold(year, month);
    }

    @Override
    public Map<Integer, Integer> purchaseOrderCount() {
        List<Integer> years = purchaseOrderRepository.findAll().stream()
                .map(order -> order.getAddedOn().getYear()).distinct().toList();

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer year : years) {
            data.put(year, purchaseOrderRepository.getPurchaseOrderCount(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> purchaseOrderCount(int year) {
        List<Integer> months = this.getPurchaseMonths(year);

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer month : months) {
            data.put(month, purchaseOrderRepository.getPurchaseOrderCount(year, month));
        }

        return data;
    }

    @Override
    public Integer purchaseOrderCount(int year, int month) {
        return purchaseOrderRepository.getPurchaseOrderCount(year, month);
    }

    @Override
    public Map<Integer, Integer> saleOrderCount() {
        List<Integer> years = saleRepository.findAll().stream()
                .map(order -> order.getAddedOn().getYear()).distinct().toList();

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer year : years) {
            data.put(year, saleRepository.getSaleOrderCount(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Integer> saleOrderCount(int year) {
        List<Integer> months = this.getSaleMonths(year);

        Map<Integer, Integer> data = new HashMap<>();
        for (Integer month : months) {
            data.put(month, saleRepository.getSaleOrderCount(year, month));
        }

        return data;
    }

    @Override
    public Integer saleOrderCount(int year, int month) {
        return saleRepository.getSaleOrderCount(year, month);
    }
}
