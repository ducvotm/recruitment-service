package vn.unigap.api.controller;

import jakarta.validation.Valid;

import org.springframework.cache.annotation.Cacheable;
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
    public ResponseEntity<ApiResponse<JobDtoOut>> createJob(@RequestBody @Valid JobDtoIn jobDtoIn) {
        JobDtoOut createdJob = jobService.create(jobDtoIn);

        // Build the success response using the static method
        ApiResponse<JobDtoOut> response = ApiResponse.success(createdJob);

        // Return the response
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    /*Update job*/
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<JobDtoOut>> updateJob(@PathVariable Long id, @RequestBody @Valid JobDtoIn jobDtoIn) {
        JobDtoOut updatedJob = jobService.update(id, jobDtoIn);

        // Build the success response using the static method
        ApiResponse<JobDtoOut> response = ApiResponse.success(updatedJob);

        // Return the response
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    /*Get job by id*/
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<JobDtoOut>> getJobById(@PathVariable Long id) {
        JobDtoOut gotJob = jobService.get(id);

        // Build the success response using the static method
        ApiResponse<JobDtoOut> response = ApiResponse.success(gotJob);

        // Return the response
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /*Get list of job*/
    @GetMapping()
    public ResponseEntity<ApiResponse<PageDtoOut<JobDtoOut>>> getListOfJob(@Valid PageDtoIn pageDtoIn) {
        PageDtoOut<JobDtoOut> listOfJob = jobService.list(pageDtoIn);

        // Build the success response using the static method
        ApiResponse<PageDtoOut<JobDtoOut>> response = ApiResponse.success(listOfJob);

        // Return the response
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /*Delete job by id*/
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob (@PathVariable Long id) {
        jobService.delete(id);

        // Build the success response using the static method
        ApiResponse<Void> response = ApiResponse.success(null);

        // Return the response
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}







