package vn.unigap.api.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.ApiResponse;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.service.JobService;


@RestController
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /*Create job*/
    @PostMapping()
    public ResponseEntity<ApiResponse<JobDtoOut>> createJob(@RequestBody JobDtoIn jobDtoIn) {
        JobDtoOut createdJob = jobService.create(jobDtoIn);

        // Build the success response using the static method
        ApiResponse<JobDtoOut> response = ApiResponse.success(createdJob);

        // Return the response
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}







