package io.github.hossensyedriadh.InvenTrackRESTfulService.controller.resource.v1;

import io.github.hossensyedriadh.InvenTrackRESTfulService.service.count.CountService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import io.swagger.v3.oas.annotations.Operation;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/count", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CountController {
    private final CountService countService;

    @Autowired
    public CountController(CountService countService) {
        this.countService = countService;
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                      "total": 685000.0
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Total sales", description = "Returns total amount of sales")
    @GetMapping("/total-sales")
    public ResponseEntity<?> totalSales() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", countService.getTotalSales());

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                      "total": 512000.0
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Total costs", description = "Returns total amount of costs")
    @GetMapping("/total-cost")
    public ResponseEntity<?> totalCost() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", countService.getTotalCost());

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                      "total": 1200
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Total total quantity sold", description = "Returns total quantity sold")
    @GetMapping("/total-sold")
    public ResponseEntity<?> totalSold() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", countService.getProductsSold());

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }

    @ApiResponse(code = 200, message = "OK", examples = @Example(value = {
            @ExampleProperty(mediaType = MediaType.APPLICATION_JSON_VALUE, value = """
                    {
                      "total": 750
                    }
                    """)
    }))
    @Operation(method = "GET", summary = "Total amount worth of stock available", description = "Returns total amount worth of stock available")
    @GetMapping("/total-stock")
    public ResponseEntity<?> totalStock() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", countService.getStockAvailable());

        return new ResponseEntity<>(jsonObject.toJSONString(), HttpStatus.OK);
    }
}
