package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.service.finance.FinanceService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping(value = "/v1/finances", produces = MediaType.APPLICATION_JSON_VALUE)
public class FinanceController {
    private final FinanceService financeService;

    @Autowired
    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                [
                                    "2020",
                                    "2021",
                                    "2022"
                                ]
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get list of years", description = "Returns a list of years from finance records")
    @GetMapping("/years/")
    public ResponseEntity<?> years() {
        List<Integer> years = financeService.getYears();

        if (years.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(years, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 244000.0,
                                "2021": 157000.0,
                                "2022": 25000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get costs of every year", description = "Returns a list of total costs in every year")
    @GetMapping("/costs/")
    public ResponseEntity<?> costs() {
        Map<Integer, Double> data = financeService.getCostsData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "9": 196000.0,
                                "10": 48000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get costs in every month by year",
            description = "Returns a list of costs in every month by given year", parameters = {@Parameter(name = "year", required = true)})
    @GetMapping(value = "/costs", params = {"year"})
    public ResponseEntity<?> costs(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getCostsData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 72000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get sales of every year", description = "Returns a list of total sales in every year")
    @GetMapping(value = "/sales/")
    public ResponseEntity<?> revenue() {
        Map<Integer, Double> data = financeService.getRevenueData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "9": 72000.0,
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get sales in every month by year",
            description = "Returns a list of sales in every month by given year", parameters = {@Parameter(name = "year", required = true)})
    @GetMapping(value = "/sales", params = {"year"})
    public ResponseEntity<?> revenue(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getRevenueData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 172000.0,
                                "2021": 157000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get profits of every year", description = "Returns a list of total profits in every year")
    @GetMapping("/profits/")
    public ResponseEntity<?> profits() {
        Map<Integer, Double> data = financeService.getProfitsData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "9": 124000.0,
                                "10": 48000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get profits in every month by year",
            description = "Returns a list of profits in every month by given year", parameters = {@Parameter(name = "year", required = true)})
    @GetMapping(value = "/profits", params = {"year"})
    public ResponseEntity<?> profits(@RequestParam("year") Integer year) {
        Map<Integer, Double> data = financeService.getProfitsData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": 70.491803,
                                "2021": 100.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get return-on-investment of every year", description = "Returns a list of return-on-investments in every year")
    @GetMapping(value = "/roi/")
    public ResponseEntity<?> returnOnInvestment() {
        Map<Integer, Double> data = financeService.getReturnOnInvestmentData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "9": 63.26530612244898,
                                "10": 100.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get return-on-investment in every month by year",
            description = "Returns a list of return-on-investment in every month by given year", parameters = {@Parameter(name = "year", required = true)})
    @GetMapping(value = "/roi", params = {"year"})
    public ResponseEntity<?> returnOnInvestment(@RequestParam("year") int year) {
        Map<Integer, Double> data = financeService.getReturnOnInvestmentData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "revenue": 401000.0,
                                "expense": 72000.0,
                                "profit": 329000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get consolidated summary of all time", description = "Returns a consolidated summary of all time")
    @GetMapping(value = "/summary/")
    public ResponseEntity<?> summary() {
        Map<String, Double> data = financeService.getSummaryData();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "2020": {
                                    "revenue": 244000.0,
                                    "expense": 72000.0,
                                    "profit": 172000.0
                                },
                                "2021": {
                                    "revenue": 157000,
                                    "expense": 157000.0,
                                    "profit": 314000.0
                                }
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get summary of every year", description = "Returns a summary of every year")
    @GetMapping(value = "/historical-summary")
    public ResponseEntity<?> allSummary() {
        Map<Integer, Map<String, Double>> data = financeService.getEveryYearSummary();

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
                    @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                            {
                                "revenue": 244000.0,
                                "expense": 72000.0,
                                "profit": 172000.0
                            }
                            """)
            })),
            @ApiResponse(code = 204, message = "No Content")
    })
    @Operation(method = "GET", summary = "Get consolidated summary of a year",
            description = "Returns a consolidated summary of a given year", parameters = {@Parameter(name = "year", required = true)})
    @GetMapping(value = "/summary", params = {"year"})
    public ResponseEntity<?> summary(@RequestParam("year") int year) {
        Map<String, Double> data = financeService.getSummaryData(year);

        if (data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }
}
