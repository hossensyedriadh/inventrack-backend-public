package io.github.hossensyedriadh.InvenTrackRESTfulService.service.finance;

import io.github.hossensyedriadh.InvenTrackRESTfulService.entity.FinanceRecord;
import io.github.hossensyedriadh.InvenTrackRESTfulService.enumerator.FinanceRecordType;
import io.github.hossensyedriadh.InvenTrackRESTfulService.repository.FinanceRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public final class FinanceServiceImpl implements FinanceService {
    private final ObjectFactory<FinanceRepository> financeRepositoryObjectFactory;

    @Autowired
    public FinanceServiceImpl(ObjectFactory<FinanceRepository> financeRepositoryObjectFactory) {
        this.financeRepositoryObjectFactory = financeRepositoryObjectFactory;
    }

    private List<Integer> getCostMonths(int year) {
        List<FinanceRecord> records = financeRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.EXPENSE) && record.getYear() == year).toList();

        List<Integer> months = new ArrayList<>();
        for (FinanceRecord record : records) {
            months.add(record.getMonth());
        }
        months = months.stream().distinct().toList();

        return months;
    }

    private List<Integer> getSaleMonths(int year) {
        List<FinanceRecord> records = financeRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.SALE) && record.getYear() == year).toList();

        List<Integer> months = new ArrayList<>();
        for (FinanceRecord record : records) {
            months.add(record.getMonth());
        }
        months = months.stream().distinct().toList();

        return months;
    }

    private List<Integer> getExpenseYears() {
        List<FinanceRecord> expenseRecords = financeRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.EXPENSE)).toList();

        List<Integer> expenseYears = new ArrayList<>();
        for (FinanceRecord record : expenseRecords) {
            expenseYears.add(record.getYear());
        }
        expenseYears = expenseYears.stream().distinct().toList();

        return expenseYears;
    }

    @Override
    public List<Integer> getYears() {
        return financeRepositoryObjectFactory.getObject().findAll().stream().map(FinanceRecord::getYear).distinct().toList();
    }

    @Override
    public Map<Integer, Double> getCostsData() {
        List<Integer> years = financeRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.EXPENSE))
                .map(FinanceRecord::getYear).distinct().toList();

        Map<Integer, Double> data = new HashMap<>();

        for (Integer year : years) {
            data.put(year, financeRepositoryObjectFactory.getObject().getExpensesByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getCostsData(int year) {
        List<Integer> months = this.getCostMonths(year);

        Map<Integer, Double> data = new HashMap<>();

        for (Integer month : months) {
            data.put(month, financeRepositoryObjectFactory.getObject().getExpensesByMonth(month));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getRevenueData() {
        List<Integer> years = financeRepositoryObjectFactory.getObject().findAll()
                .stream().filter(record -> record.getType().equals(FinanceRecordType.SALE))
                .map(FinanceRecord::getYear).distinct().toList();

        Map<Integer, Double> data = new HashMap<>();

        for (Integer year : years) {
            data.put(year, financeRepositoryObjectFactory.getObject().getSalesByYear(year));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getRevenueData(int year) {
        List<Integer> months = this.getSaleMonths(year);

        Map<Integer, Double> data = new HashMap<>();

        for (Integer month : months) {
            data.put(month, financeRepositoryObjectFactory.getObject().getSalesByMonth(month));
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getProfitsData() {
        List<Integer> expenseYears = this.getExpenseYears();

        Map<Integer, Double> expenseData = this.getCostsData();
        Map<Integer, Double> saleData = this.getRevenueData();

        Map<Integer, Double> data = new HashMap<>();

        for (Integer expenseYear : expenseYears) {
            if (saleData.containsKey(expenseYear)) {
                data.put(expenseYear, saleData.get(expenseYear) - expenseData.get(expenseYear));
            } else {
                data.put(expenseYear, 0 - expenseData.get(expenseYear));
            }
        }

        return data;
    }

    @Override
    public Map<Integer, Double> getProfitsData(int year) {
        List<Integer> expenseMonths = this.getCostMonths(year);
        Map<Integer, Double> expenseData = this.getCostsData(year);
        Map<Integer, Double> saleData = this.getRevenueData(year);

        Map<Integer, Double> data = new HashMap<>();
        for (Integer expenseMonth : expenseMonths) {
            if (saleData.containsKey(expenseMonth)) {
                data.put(expenseMonth, saleData.get(expenseMonth) - expenseData.get(expenseMonth));
            } else {
                data.put(expenseMonth, 0 - expenseData.get(expenseMonth));
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
    public Map<Integer, Map<String, Double>> getEveryYearSummary() {
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
