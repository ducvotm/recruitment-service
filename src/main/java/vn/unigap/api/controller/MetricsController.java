package vn.unigap.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.common.response.ApiResponse;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;
import vn.unigap.api.service.MetricService;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<MetricsByDateDtoOut>> getMetricsByDate(@RequestBody @Valid MetricsByDateDtoIn metricsByDateDtoIn) {
        MetricsByDateDtoOut employerByDateResult = metricService.getMetricsByDate(metricsByDateDtoIn);

        // Build the success response using the static method
        ApiResponse<MetricsByDateDtoOut> response = ApiResponse.success(employerByDateResult);

        // Return the response
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
