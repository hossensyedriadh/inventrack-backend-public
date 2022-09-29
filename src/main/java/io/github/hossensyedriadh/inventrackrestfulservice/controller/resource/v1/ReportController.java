package io.github.hossensyedriadh.inventrackrestfulservice.controller.resource.v1;

import io.github.hossensyedriadh.inventrackrestfulservice.service.report.ReportService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/v1/reports", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/units-sold/")
    public ResponseEntity<?> unitsSold() {
        Map<Integer, Integer> unitsSold = reportService.unitsSold();

        if (unitsSold.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(unitsSold, HttpStatus.OK);
    }

    @GetMapping(value = "/units-sold/{year}")
    public ResponseEntity<?> unitsSold(@PathVariable("year") int year) {
        Map<Integer, Integer> unitsSold = reportService.unitsSold(year);

        if (unitsSold.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(unitsSold, HttpStatus.OK);
    }

    @GetMapping(value = "/units-sold/{year}", params = {"month"})
    public ResponseEntity<?> unitsSold(@PathVariable("year") int year, @RequestParam("month") int month) {
        Integer unitsSold = reportService.unitsSold(year, month);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", unitsSold);

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @GetMapping("/purchase-order-count/")
    public ResponseEntity<?> purchaseOrderCount() {
        Map<Integer, Integer> count = reportService.purchaseOrderCount();

        if (count.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/purchase-order-count/{year}")
    public ResponseEntity<?> purchaseOrderCount(@PathVariable("year") int year) {
        Map<Integer, Integer> count = reportService.purchaseOrderCount(year);

        if (count.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping(value = "/purchase-order-count/{year}", params = {"month"})
    public ResponseEntity<?> purchaseOrderCount(@PathVariable("year") int year, @RequestParam("month") int month) {
        Integer count = reportService.purchaseOrderCount(year, month);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", count);

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @GetMapping("/sale-order-count/")
    public ResponseEntity<?> saleOrderCount() {
        Map<Integer, Integer> count = reportService.saleOrderCount();

        if (count.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/sale-order-count/{year}")
    public ResponseEntity<?> saleOrderCount(@PathVariable("year") int year) {
        Map<Integer, Integer> count = reportService.saleOrderCount(year);

        if (count.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping(value = "/sale-order-count/{year}", params = {"month"})
    public ResponseEntity<?> saleOrderCount(@PathVariable("year") int year, @RequestParam("month") int month) {
        Integer count = reportService.saleOrderCount(year, month);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count", count);

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }
}
