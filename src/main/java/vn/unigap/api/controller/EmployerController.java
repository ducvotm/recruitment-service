package vn.unigap.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.unigap.api.common.ApiException;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.ApiResponse;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.dto.out.UpdateEmployerDtoOut;
import vn.unigap.api.service.EmployerService;


@RestController
@RequestMapping("/employer")
public class EmployerController {

    private final EmployerService employerService;

    public EmployerController(EmployerService employerService) {
        this.employerService = employerService;
    }

    /*Create employer*/
    @PostMapping()
    public ResponseEntity<ApiResponse<EmployerDtoOut>> createEmployer(@RequestBody EmployerDtoIn employerDtoIn) {
        try {
            EmployerDtoOut createdEmployer = employerService.create(employerDtoIn);

            // Build the success response using the static method
            ApiResponse<EmployerDtoOut> response = ApiResponse.success(createdEmployer);

            // Return the response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (ApiException e) {

            // Build the error response using the static method
            ApiResponse<EmployerDtoOut> response = ApiResponse.error(
                    e.getErrorCode(),
                    e.getHttpStatus(),
                    e.getMessage()
            );

            // Return the error response
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
        }

    }

    /*Update employer*/
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<UpdateEmployerDtoOut>> updateEmployer(@PathVariable Long id, @RequestBody EmployerDtoIn employerDtoIn) {
        try {
            UpdateEmployerDtoOut updatedEmployer = employerService.update(id, employerDtoIn);

            // Build the success response using the static method
            ApiResponse<UpdateEmployerDtoOut> response = ApiResponse.success(updatedEmployer);

            // Return the response
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);

        } catch (ApiException e) {

            // Build the error response using the static method
            ApiResponse<UpdateEmployerDtoOut> response = ApiResponse.error(
                    e.getErrorCode(),
                    e.getHttpStatus(),
                    e.getMessage()
            );

            // Return the error response
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
        }
    }

    /*Get employer by id*/
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<EmployerDtoOut>> getEmployerById(@PathVariable Long id) {
        try {
            EmployerDtoOut gotEmployer = employerService.get(id);

            // Build the success response using the static method
            ApiResponse<EmployerDtoOut> response = ApiResponse.success(gotEmployer);

            // Return the response
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);

        } catch (ApiException e) {

            // Build the error response using the static method
            ApiResponse<EmployerDtoOut> response = ApiResponse.error(
                    e.getErrorCode(),
                    e.getHttpStatus(),
                    e.getMessage()
            );

            // Return the error response
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
        }
    }

    /*Get list of employer*/
    @GetMapping()
    public ResponseEntity<ApiResponse<PageDtoOut<EmployerDtoOut>>> getListOfEmployer(@Valid PageDtoIn pageDtoIn) {
        PageDtoOut<EmployerDtoOut> listOfEmployer = employerService.list(pageDtoIn);

        // Build the success response using the static method
        ApiResponse<PageDtoOut<EmployerDtoOut>> response = ApiResponse.success(listOfEmployer);

        // Return the response
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /*Delete employer by id*/
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployer (@PathVariable Long id) {
        try {
            employerService.delete(id);

            // Build the success response using the static method
            ApiResponse<Void> response = ApiResponse.success(null);

            // Return the response
            return ResponseEntity.status(HttpStatus.OK)
                    .body(response);

        } catch (ApiException e) {

            // Build the error response using the static method
            ApiResponse<Void> response = ApiResponse.error(
                    e.getErrorCode(),
                    HttpStatus.CONFLICT,
                    e.getMessage()
            );

            // Return the error response
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
        }
    }
}






