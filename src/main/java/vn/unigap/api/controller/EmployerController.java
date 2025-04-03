package vn.unigap.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;

import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.service.EmployerService;
import vn.unigap.common.controller.AbstractResponseController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/employer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Employer", description = "Employer management")
@SecurityRequirement(name = "Authorization")
public class EmployerController extends AbstractResponseController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @Operation(summary = "Create new employer", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEmployer.class))) })
    @PostMapping(value = "")
    public ResponseEntity<?> create(@RequestBody EmployerDtoIn employerDtoIn) {
        return responseEntity(() -> {
            return this.employerService.create(employerDtoIn);
        }, HttpStatus.CREATED);
    }

    @Operation(summary = "Update employer's information", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "aplication/json", schema = @Schema(implementation = ResponseEmployer.class))) })
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid EmployerDtoIn employerDtoIn) {
        return responseEntity(() -> {
            return this.employerService.update(id, employerDtoIn);
        });
    }

    @Operation(summary = "Get employer by ID", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEmployer.class))) })
    @GetMapping(value = "/{id}", consumes = MediaType.ALL_VALUE) // To adjust the attribute tha has already set in the
                                                                 // @RequestMapping
    public ResponseEntity<?> get(@PathVariable Long id) {
        return responseEntity(() -> {
            return this.employerService.get(id);
        });
    }

    @Operation(summary = "List all employers", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponsePageEmployer.class))) })
    @GetMapping(value = "", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> list(@Valid PageDtoIn pageDtoIn) {
        return responseEntity(() -> {
            return this.employerService.list(pageDtoIn);
        });
    }

    @Operation(summary = "Delete job", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = vn.unigap.common.response.ApiResponse.class))) })
    @DeleteMapping(value = "{id}", consumes = MediaType.ALL_VALUE) //
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return responseEntity(() -> {
            this.employerService.delete(id);
            return new HashMap<>();
        });
    }

    // Internal Response classes for Swagger documentation
    private static class ResponseEmployer extends vn.unigap.common.response.ApiResponse<EmployerDtoOut> {
    }

    private static class ResponsePageEmployer
            extends vn.unigap.common.response.ApiResponse<PageDtoOut<EmployerDtoOut>> {
    }
}