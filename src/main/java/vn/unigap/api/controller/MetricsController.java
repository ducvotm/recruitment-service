package vn.unigap.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;
import vn.unigap.api.service.MetricService;
import vn.unigap.common.controller.AbstractResponseController;

@RestController
@RequestMapping("/metrics")
@Tag(name = "Metrics", description = "Metrics management")
public class MetricsController extends AbstractResponseController {

    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    @Operation(summary = "Get metrics by date", responses = {
            @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMetricsByDate.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Metrics not found", content = @Content(mediaType = "application/json"))})
    @GetMapping
    public ResponseEntity<?> getMetricsByDate(@RequestBody @Valid MetricsByDateDtoIn metricsByDateDtoIn) {
        return responseEntity(() -> {
            MetricsByDateDtoOut result = metricService.getMetricsByDate(metricsByDateDtoIn);
            return result;
        }, HttpStatus.OK);
    }

    // Internal Response class for Swagger documentation
    private static class ResponseMetricsByDate extends vn.unigap.common.response.ApiResponse<MetricsByDateDtoOut> {
    }
}
