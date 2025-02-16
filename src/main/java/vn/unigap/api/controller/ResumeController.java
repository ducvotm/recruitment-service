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
import vn.unigap.api.dto.in.ResumeDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.ResumeDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.service.ResumeService;
import vn.unigap.common.controller.AbstractResponseController;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/resume", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Resume", description = "Resume management")
@SecurityRequirement(name = "Authorization")
public class ResumeController extends AbstractResponseController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @Operation(summary = "List all Resumes", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ResponsePageResume.class)))})
    @GetMapping(value = "", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> list(@Valid PageDtoIn pageDtoIn) {
        return responseEntity(() -> {
            return this.resumeService.list(pageDtoIn);
        });
    }

    @Operation(summary = "Get Resume by ID", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseResume.class)))})
    @GetMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) {
        return responseEntity(() -> {
            return this.resumeService.get(id);
        });
    }

    @Operation(summary = "Create a new Resume", responses = {
            @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseResume.class)))})
    @PostMapping(value = "")
    public ResponseEntity<?> create(@RequestBody @Valid ResumeDtoIn ResumeDtoIn) {
        return responseEntity(() -> {
            return this.resumeService.create(ResumeDtoIn);
        }, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Resume", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseResume.class)))})
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> update(@PathVariable(value = "id") Long id, @RequestBody @Valid ResumeDtoIn ResumeDtoIn) {
        return responseEntity(() -> {
            return this.resumeService.update(id, ResumeDtoIn);
        });
    }

    @Operation(summary = "Delete Resume", responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = vn.unigap.common.response.ApiResponse.class)))})
    @DeleteMapping(value = "/{id}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
        return responseEntity(() -> {
            this.resumeService.delete(id);
            return new HashMap<>();
        });
    }

    // Internal Response classes for Swagger documentation
    private static class ResponseResume extends vn.unigap.common.response.ApiResponse<ResumeDtoOut> {
    }

    private static class ResponsePageResume extends vn.unigap.common.response.ApiResponse<PageDtoOut<ResumeDtoOut>> {
    }
}
