package io.github.hossensyedriadh.inventrackrestfulservice.service.finance;

import io.github.hossensyedriadh.inventrackrestfulservice.entity.FinanceRecord;
import io.github.hossensyedriadh.inventrackrestfulservice.enumerator.FinanceRecordType;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.FinanceRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.ProductRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SaleItemRepository;
import io.github.hossensyedriadh.inventrackrestfulservice.repository.jpa.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinanceServiceImpl implements FinanceService {
    private final FinanceRepository financeRepository;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public FinanceServiceImpl(FinanceRepository financeRepository, SaleRepository saleRepository,
                              SaleItemRepository saleItemRepository, ProductRepository productRepository) {
        this.financeRepository = financeRepository;
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
    }

    private List<Integer> getCostMonths(int year) {
        return financeRepository.findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.EXPENSE) && record.getYear() == year)
                .map(FinanceRecord::getMonth).distinct().toList();
    }

    private List<Integer> getSaleMonths(int year) {
        return financeRepository.findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.SALE) && record.getYear() == year)
                .map(FinanceRecord::getMonth).distinct().toList();
    }

    private List<Integer> getExpenseYears() {
        return financeRepository.findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.EXPENSE))
                .map(FinanceRecord::getYear).distinct().toList();
    }

    @Override
    public List<Integer> getFinanceYears() {
        return financeRepository.findAll().stream().map(FinanceRecord::getYear).distinct().collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Double> getCostsData() {
        List<Integer> years = financeRepository.findAll().stream()
                .filter(record -> record.getType().equals(FinanceRecordType.EXPENSE))
                .map(FinanceRecord::getYear).distinct().toList();

        Map<Integer, Double> data = new HashMap<>();

        for (Integer year : years) {
            data.put(year, financeRepository.getExpensesByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getCostsData(int year) {
        List<Integer> months = this.getCostMonths(year);

        Map<Integer, Double> data = new HashMap<>();

        for (Integer month : months) {
            data.put(month, financeRepository.getExpensesByMonth(month));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getRevenueData() {
        List<Integer> years = financeRepository.findAll().stream()
                .filter(record -> record.getType().equals(FinanceRecordType.SALE))
                .map(FinanceRecord::getYear).distinct().toList();

        Map<Integer, Double> data = new HashMap<>();

        for (Integer year : years) {
            data.put(year, financeRepository.getSalesByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getRevenueData(int year) {
        List<Integer> months = this.getSaleMonths(year);

        Map<Integer, Double> data = new HashMap<>();

        for (Integer month : months) {
            data.put(month, financeRepository.getSalesByMonth(month));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getProfitsData() {
        List<Integer> expenseYears = this.getExpenseYears();
        Map<Integer, Double> saleData = this.getRevenueData();
        Map<Integer, Double> data = new HashMap<>();

        for (Integer expenseYear : expenseYears) {
            if (saleData.containsKey(expenseYear)) {
                Double profits = saleRepository.findAll().stream().filter(record -> record.getAddedOn().getYear() == expenseYear)
                        .peek(record -> record.setProducts(saleItemRepository.findAll().stream().filter(s -> s.getSale().getId().equals(record.getId())).toList()))
                        .flatMap(record -> record.getProducts().stream().map(product -> (product.getPrice() * product.getQuantity())
                                - (productRepository.findAll().stream().filter(p -> p.getPurchaseOrder().getAddedOn().getYear() == expenseYear)
                                .map(p -> ((p.getPurchaseOrder().getTotalPurchasePrice() + p.getPurchaseOrder().getShippingCosts()
                                        + p.getPurchaseOrder().getOtherCosts()) / p.getPurchaseOrder().getQuantity()))
                                .mapToDouble(c -> c).sum()))).mapToDouble(p -> p).sum();

                data.put(expenseYear, profits);
            } else {
                data.put(expenseYear, 0.0);
            }
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getProfitsData(int year) {
        List<Integer> expenseMonths = this.getCostMonths(year);
        Map<Integer, Double> saleData = this.getRevenueData(year);

        Map<Integer, Double> data = new HashMap<>();
        for (Integer expenseMonth : expenseMonths) {
            if (saleData.containsKey(expenseMonth)) {
                Double profits = saleRepository.findAll().stream().filter(record -> record.getAddedOn().getYear() == year
                                && record.getAddedOn().getMonthValue() == expenseMonth)
                        .peek(record -> record.setProducts(saleItemRepository.findAll().stream()
                                .filter(s -> s.getSale().getId().equals(record.getId())).toList())).flatMap(record -> record.getProducts()
                                .stream().map(product -> (product.getPrice() * product.getQuantity())
                                        - (productRepository.findAll().stream().filter(p -> p.getPurchaseOrder().getAddedOn().getYear() == year
                                                && record.getAddedOn().getMonthValue() == expenseMonth)
                                        .map(p -> ((p.getPurchaseOrder().getTotalPurchasePrice()
                                                + p.getPurchaseOrder().getShippingCosts()
                                                + p.getPurchaseOrder().getOtherCosts()) / p.getPurchaseOrder().getQuantity()))
                                        .mapToDouble(c -> c).sum()))).mapToDouble(p -> p).sum();
                data.put(expenseMonth, profits);
            } else {
                data.put(expenseMonth, 0.0);
            }
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getReturnOnInvestmentData() {
        List<Integer> expenseYears = this.getExpenseYears();
        Map<Integer, Double> expenses = this.getCostsData();
        Map<Integer, Double> profits = this.getProfitsData();

        Map<Integer, Double> data = new HashMap<>();

        for (Integer expenseYear : expenseYears) {
            data.put(expenseYear, (profits.get(expenseYear) / expenses.get(expenseYear)) * 100);
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getReturnOnInvestmentData(int year) {
        List<Integer> expenseMonths = this.getCostMonths(year);
        Map<Integer, Double> expenses = this.getCostsData(year);
        Map<Integer, Double> profits = this.getProfitsData(year);

        Map<Integer, Double> data = new HashMap<>();

        for (Integer month : expenseMonths) {
            data.put(month, (profits.get(month) / expenses.get(month)) * 100);
        }

        return data;
    }

    @Override
    public Map<String, Double> getSummaryData() {
        Map<Integer, Double> expensesData = this.getCostsData();
        Map<Integer, Double> revenueData = this.getRevenueData();
        Map<Integer, Double> profitsData = this.getProfitsData();

        Map<String, Double> data = new HashMap<>();
        data.put("expense", expensesData.values().stream().mapToDouble(m -> m).sum());
        data.put("revenue", revenueData.values().stream().mapToDouble(m -> m).sum());
        data.put("profit", profitsData.values().stream().mapToDouble(m -> m).sum());

        return data;
    }

    @Override
    public Map<Integer, Map<String, Double>> getHistoricalSummary() {
        List<Integer> expenseYears = this.getExpenseYears();

        Map<Integer, Map<String, Double>> totalData = new HashMap<>();
        for (Integer expenseYear : expenseYears) {
            Map<String, Double> data = new HashMap<>();
            data.put("expense", this.getCostsData(expenseYear).values().stream().mapToDouble(m -> m).sum());
            data.put("revenue", this.getRevenueData(expenseYear).values().stream().mapToDouble(m -> m).sum());
            data.put("profit", this.getProfitsData(expenseYear).values().stream().mapToDouble(m -> m).sum());

            totalData.put(expenseYear, data);
        }

        return totalData;
    }

    @Override
    public Map<String, Double> getSummaryData(int year) {
        Map<Integer, Double> expensesData = this.getCostsData(year);
        Map<Integer, Double> revenueData = this.getRevenueData(year);
        Map<Integer, Double> profitsData = this.getProfitsData(year);

        Map<String, Double> data = new HashMap<>();
        data.put("expense", expensesData.values().stream().mapToDouble(m -> m).sum());
        data.put("revenue", revenueData.values().stream().mapToDouble(m -> m).sum());
        data.put("profit", profitsData.values().stream().mapToDouble(m -> m).sum());

        return data;
    }
}
