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
import vn.unigap.api.dto.in.SeekerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;

import vn.unigap.api.dto.in.SeekerDtoIn;
import vn.unigap.api.dto.in.UpdateSeekerDtoIn;
import vn.unigap.api.dto.out.SeekerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.service.SeekerService;
import vn.unigap.common.controller.AbstractResponseController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/seeker", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Seeker", description = "Seeker management")
@SecurityRequirement(name = "Authorization")
public class SeekerController extends AbstractResponseController {

    private final SeekerService seekerService;

    public SeekerController(SeekerService seekerService) {
        this.seekerService = seekerService;
    }

    @Operation(summary = "Create new seeker", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseSeeker.class)))})
    @PostMapping(value = "")
    public ResponseEntity<?> create(@RequestBody SeekerDtoIn seekerDtoIn) {
        return responseEntity(() -> {
            return this.seekerService.create(seekerDtoIn);
        }, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Seeker's information", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "aplication/json", schema = @Schema(implementation = ResponseSeeker.class)))})
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UpdateSeekerDtoIn updateSeekerDtoIn) {
        return responseEntity(() -> {
            return this.seekerService.update(id, updateSeekerDtoIn);
        });
    }

    @Operation(summary = "Get Seeker by ID", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseSeeker.class)))})
    @GetMapping(value = "/{id}", consumes = MediaType.ALL_VALUE) // To adjust the attribute tha has already set in the @RequestMapping
    public ResponseEntity<?> get(@PathVariable Long id) {
        return responseEntity(() -> {
            return this.seekerService.get(id);
        });
    }

    @Operation(summary = "List all Seekers", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponsePageSeeker.class)))})
    @GetMapping(value = "", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> list(@Valid PageDtoIn pageDtoIn) {
        return responseEntity(() -> {
            return this.seekerService.list(pageDtoIn);
        });
    }

    @Operation(summary = "Delete job", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = vn.unigap.common.response.ApiResponse.class)))})
    @DeleteMapping(value = "{id}", consumes = MediaType.ALL_VALUE) //
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return responseEntity(() -> {
            this.seekerService.delete(id);
            return new HashMap<>();
        });
    }

    // Internal Response classes for Swagger documentation
    private static class ResponseSeeker extends vn.unigap.common.response.ApiResponse<SeekerDtoOut> {
    }

    private static class ResponsePageSeeker
            extends
            vn.unigap.common.response.ApiResponse<PageDtoOut<SeekerDtoOut>> {
    }
}