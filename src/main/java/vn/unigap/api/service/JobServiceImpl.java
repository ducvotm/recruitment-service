package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import vn.unigap.common.exception.ApiException;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.entity.jpa.Job;
import vn.unigap.api.repository.jpa.EmployerRepository;
import vn.unigap.api.repository.jpa.FieldRepository;
import vn.unigap.api.repository.jpa.JobRepository;


@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final FieldRepository fieldRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, EmployerRepository employerRepository,
            FieldRepository fieldRepository, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
        this.fieldRepository = fieldRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public JobDtoOut create(JobDtoIn jobDtoIn) {

        // Validate if employer, the fields and provinces exist
        validateEmployerFieldAndProvinceExistence(jobDtoIn);

        // Create and save the Job entity
        Job job = jobRepository.save(Job.builder().title(jobDtoIn.getTitle()).employerId(jobDtoIn.getEmployerId())
                .quantity(jobDtoIn.getQuantity()).description(jobDtoIn.getDescription()).fields(jobDtoIn.getFieldIds())
                .provinces(jobDtoIn.getProvinceIds()).salary(jobDtoIn.getSalary()).expiredAt(jobDtoIn.getExpiredAt())
                .build());

        // Convert Entity to DTO
        return JobDtoOut.from(job);
    }

    @Override
    public JobDtoOut update(Long id, JobDtoIn jobDtoIn) {
        // Fetch the existing job entity
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Job not found"));

        // Validate if the fields and provinces exist
        validateEmployerFieldAndProvinceExistence(jobDtoIn);

        // Update and save the Job entity
        Job updatedjob = jobRepository
                .save(Job.builder().title(jobDtoIn.getTitle()).employerId(jobDtoIn.getEmployerId())
                        .quantity(jobDtoIn.getQuantity()).description(jobDtoIn.getDescription())
                        .fields(jobDtoIn.getFieldIds()).provinces(jobDtoIn.getProvinceIds())
                        .salary(jobDtoIn.getSalary()).expiredAt(jobDtoIn.getExpiredAt()).build());

        // Convert updated entity to DTO
        return JobDtoOut.from(updatedjob);
    }

    @Override
    @Cacheable(value = "jobs", key = "#id")
    public JobDtoOut get(Long id) {

        System.out.println("Fetching job with id: " + id);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Job not found"));

        JobDtoOut jobDtoOut = JobDtoOut.from(job);
        System.out.println("Returning jobDtoOut: " + jobDtoOut);

        return jobDtoOut;
    }

    /* Copy from sample projects */
    @Override
    public PageDtoOut<JobDtoOut> list(PageDtoIn pageDtoIn) {

        Page<Job> jobs = this.jobRepository.findAllJobsOrderedByExpiredAtAndEmployerName(
                PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getPageSize()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), jobs.getTotalElements(),
                jobs.stream().map(JobDtoOut::from).toList());
    }

    @Override
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));
        jobRepository.delete(job);
    }

    private void validateEmployerFieldAndProvinceExistence(JobDtoIn jobDtoIn) {

        // Check if the employer exists
        if (!employerRepository.existsById(jobDtoIn.getEmployerId())) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "The employer does not exist");
        }

        // Check if the fields exist
        String[] fieldIdsArray = jobDtoIn.getFieldIds().split("-");
        for (String fieldId : fieldIdsArray) {
            if (!fieldRepository.existsById(Long.valueOf(fieldId))) {
                throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST,
                        "The field with ID " + fieldId + " does not exist");
            }
        }

 /*       // Check if the provinces exist
        String[] provinceIdsArray = jobDtoIn.getProvinceIds().split("-");
        for (String provinceId : provinceIdsArray) {
            if (!provinceRepository.existsById(Long.valueOf(provinceId))) {
                throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST,
                        "The province with ID " + provinceId + " does not exist");
            }
        }*/
    }
}
