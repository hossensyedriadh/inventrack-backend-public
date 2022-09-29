package io.github.hossensyedriadh.inventrackrestfulservice.service.finance;

import java.util.List;
import java.util.Map;

public interface FinanceService {
    List<Integer> getFinanceYears();

    Map<Integer, Double> getCostsData();

    Map<Integer, Double> getCostsData(int year);

    Map<Integer, Double> getRevenueData();

    Map<Integer, Double> getRevenueData(int year);

    Map<Integer, Double> getProfitsData();

    Map<Integer, Double> getProfitsData(int year);

    Map<Integer, Double> getReturnOnInvestmentData();

    Map<Integer, Double> getReturnOnInvestmentData(int year);

    Map<String, Double> getSummaryData();

    Map<Integer, Map<String, Double>> getHistoricalSummary();

    Map<String, Double> getSummaryData(int year);
}
