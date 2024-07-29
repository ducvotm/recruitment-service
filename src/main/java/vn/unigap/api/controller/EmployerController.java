package vn.unigap.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.unigap.api.common.ApiException;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.out.ApiResponse;
import vn.unigap.api.dto.out.EmployerDtoOut;
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
                    HttpStatus.CONFLICT,
                    e.getMessage()
            );

            // Return the error response
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
        }

    }
}





