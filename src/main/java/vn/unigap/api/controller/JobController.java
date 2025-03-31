package vn.unigap.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.dto.out.JobWithSeekersDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.service.JobService;
import vn.unigap.common.controller.AbstractResponseController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/job", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Job", description = "Job management")
@SecurityRequirement(name = "Authorization")
public class JobController extends AbstractResponseController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @Operation(summary = "List all jobs", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponsePageJob.class)))})
    @GetMapping(value = "", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> list(@Valid PageDtoIn pageDtoIn) {
        return responseEntity(() -> {
            return this.jobService.list(pageDtoIn);
        });
    }

    @Operation(summary = "Get job by ID", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseJob.class)))})
    @GetMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        return responseEntity(() -> {
            return this.jobService.get(id);
        });
    }

    @Operation(summary = "Create a new job", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseJob.class)))})
    @PostMapping(value = "")
    public ResponseEntity<?> create(@RequestBody @Valid JobDtoIn jobDtoIn) {
        return responseEntity(() -> {
            return this.jobService.create(jobDtoIn);
        }, HttpStatus.CREATED);
    }

    @Operation(summary = "Update job", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseJob.class)))})
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @RequestBody @Valid JobDtoIn jobDtoIn) {
        return responseEntity(() -> {
            return this.jobService.update(id, jobDtoIn);
        });
    }

    @Operation(summary = "Delete job", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = vn.unigap.common.response.ApiResponse.class)))})
    @DeleteMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        return responseEntity(() -> {
            this.jobService.delete(id);
            return new HashMap<>();
        });
    }



    // Internal Response classes for Swagger documentation
    private static class ResponseJob extends vn.unigap.common.response.ApiResponse<JobDtoOut> {
    }

    private static class ResponsePageJob extends vn.unigap.common.response.ApiResponse<PageDtoOut<JobDtoOut>> {
    }
}
