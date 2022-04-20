package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.service.report.ReportService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 215,
                                "2021": 310,
                                "2022": 165
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get count of every years' product units sold",
            description = "Returns every years' product units sale count")
    @GetMapping("/units-sold/")
    public ResponseEntity<?> unitsSold() {
        Map<Integer, Integer> unitsSold = reportService.getUnitsSold();

        if (unitsSold.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(unitsSold, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "1": 215,
                                "2": 310,
                                "3": 165
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get count of every months' product units sold in a year",
            description = "Returns every months' product units sold in a year")
    @GetMapping(value = "/units-sold", params = {"year"})
    public ResponseEntity<?> unitsSold(@RequestParam("year") int year) {
        Map<Integer, Integer> unitsSold = reportService.getUnitsSold(year);

        if (unitsSold.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(unitsSold, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 215,
                                "2021": 310,
                                "2022": 165
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get count of every years' purchase orders", description = "Returns every years' purchase order count")
    @GetMapping("/purchase-order-count/")
    public ResponseEntity<?> purchaseOrderCount() {
        Map<Integer, Integer> purchaseOrderCounts = reportService.getPurchaseOrderCount();

        if (purchaseOrderCounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(purchaseOrderCounts, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "1": 215,
                                "2": 310,
                                "3": 165
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get count of every months' purchase order in a year", description = "Returns every months' purchase order count in a year")
    @GetMapping(value = "/purchase-order-count", params = {"year"})
    public ResponseEntity<?> purchaseOrderCount(@RequestParam("year") int year) {
        Map<Integer, Integer> purchaseOrderCounts = reportService.getPurchaseOrderCount(year);

        if (purchaseOrderCounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(purchaseOrderCounts, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 215,
                                "2021": 310,
                                "2022": 165
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get count of every years' sale order", description = "Returns count of every years' sale order")
    @GetMapping("/sale-order-count/")
    public ResponseEntity<?> saleOrderCount() {
        Map<Integer, Integer> saleOrderCounts = reportService.getSaleOrderCount();

        if (saleOrderCounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(saleOrderCounts, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "1": 215,
                                "2": 310,
                                "3": 165
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get count of every months' sale order in a year", description = "Returns count of every months' sale order in a year")
    @GetMapping(value = "/sale-order-count", params = {"year"})
    public ResponseEntity<?> saleOrderCount(@RequestParam("year") int year) {
        Map<Integer, Integer> saleOrderCounts = reportService.getSaleOrderCount(year);

        if (saleOrderCounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(saleOrderCounts, HttpStatus.OK);
    }
}
