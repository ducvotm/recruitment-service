package vn.unigap.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.out.ApiResponse;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.service.EmployerServiceImpl;

@RestController
@RequestMapping("/employer")
public class EmployerController {

    private final EmployerServiceImpl employerServiceImpl;

    public EmployerController(EmployerServiceImpl employerServiceImpl) {
        this.employerServiceImpl = employerServiceImpl;
    }

    /*Create employer*/
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EmployerDtoOut>> createEmployer(@RequestBody EmployerDtoIn employerDtoIn) {
        EmployerDtoOut createdEmployer = employerServiceImpl.create(employerDtoIn);

        // Build the success response using the static method
        ApiResponse<EmployerDtoOut> response = ApiResponse.success(createdEmployer);

        // Return the response
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}





