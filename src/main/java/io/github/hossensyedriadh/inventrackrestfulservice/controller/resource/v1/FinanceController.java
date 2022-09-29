package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.service.finance.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/v1/finances", produces = {MediaType.APPLICATION_JSON_VALUE})
public class FinanceController {
    private final FinanceService financeService;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/years/")
    public ResponseEntity<?> financeYears() {
        List<Integer> years = financeService.getFinanceYears();

        if (years.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(years, HttpStatus.OK);
    }

    @GetMapping("/costs/")
    public ResponseEntity<?> costs() {
        Map<Integer, Double> data = financeService.getCostsData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/costs", params = {"year"})
    public ResponseEntity<?> costs(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getCostsData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/sales/")
    public ResponseEntity<?> revenue() {
        Map<Integer, Double> data = financeService.getRevenueData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/sales", params = {"year"})
    public ResponseEntity<?> revenue(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getRevenueData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/profits/")
    public ResponseEntity<?> profits() {
        Map<Integer, Double> data = financeService.getProfitsData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/profits", params = {"year"})
    public ResponseEntity<?> profits(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getProfitsData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/roi/")
    public ResponseEntity<?> roi() {
        Map<Integer, Double> data = financeService.getReturnOnInvestmentData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/roi", params = {"year"})
    public ResponseEntity<?> roi(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getReturnOnInvestmentData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/summary/")
    public ResponseEntity<?> summary() {
        Map<String, Double> data = financeService.getSummaryData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/summary", params = {"year"})
    public ResponseEntity<?> summary(@RequestParam("year") Integer year) {
        Map<String, Double> data = financeService.getSummaryData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/historical-summary")
    public ResponseEntity<?> allSummary() {
        Map<Integer, Map<String, Double>> data = financeService.getHistoricalSummary();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
