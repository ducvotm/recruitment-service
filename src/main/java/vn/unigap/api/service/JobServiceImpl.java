package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import vn.unigap.api.common.ApiException;
import vn.unigap.api.common.ErrorCode;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.entity.Job;
import vn.unigap.api.repository.JobRepository;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, JdbcTemplate jdbcTemplate) {
        this.jobRepository = jobRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public JobDtoOut create(JobDtoIn jobDtoIn) {

        // Validate if the fields and provinces exist
        validateEmployerFieldAndProvinceExistence(jobDtoIn);

        // Create and save the Job entity
        Job job = jobRepository.save(Job.builder()
                .title(jobDtoIn.getTitle())
                .employerId(jobDtoIn.getEmployerId())
                .quantity(jobDtoIn.getQuantity())
                .description(jobDtoIn.getDescription())
                .fields(jobDtoIn.getFieldIds())
                .provinces(jobDtoIn.getProvinceIds())
                .salary(jobDtoIn.getSalary())
                .expiredAt(jobDtoIn.getExpiredAt())
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
        Job updatedjob = jobRepository.save(Job.builder()
                .title(jobDtoIn.getTitle())
                .employerId(jobDtoIn.getEmployerId())
                .quantity(jobDtoIn.getQuantity())
                .description(jobDtoIn.getDescription())
                .fields(jobDtoIn.getFieldIds())
                .provinces(jobDtoIn.getProvinceIds())
                .salary(jobDtoIn.getSalary())
                .expiredAt(jobDtoIn.getExpiredAt())
                .build());

        // Convert updated entity to DTO
        return JobDtoOut.from(updatedjob);
    }

    @Override
    public JobDtoOut get(Long id) {
        // Check if the id is existing yet
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));

        return JobDtoOut.from(job);
    }

    /*Copy from sample projects*/
    @Override
    public PageDtoOut<JobDtoOut> list(PageDtoIn pageDtoIn) {
        Page<Job> employers = this.jobRepository
                .findAll(PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getPageSize(),
                        Sort.by("expiredAt").descending()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), employers.getTotalElements(),
                employers.stream().map(JobDtoOut::from).toList());
    }

    @Override
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));
        jobRepository.delete(job);
    }

    private void validateEmployerFieldAndProvinceExistence(JobDtoIn jobDtoIn) {

        // Check if the job exists
        String jobCheckSql = "SELECT COUNT(*) FROM employer WHERE id = ?";
        Integer jobCount = jdbcTemplate.queryForObject(jobCheckSql, Integer.class, jobDtoIn.getEmployerId());
        if (jobCount == null || jobCount == 0) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Employer does not exist");
        } else {
            // Check if the fields exist
            String[] fieldIdsArray = jobDtoIn.getFieldIds().split("-");
            for (String fieldId : fieldIdsArray) {
                String fieldCheckSql = "SELECT COUNT(*) FROM job_field WHERE id = ?";
                Integer fieldCount = jdbcTemplate.queryForObject(fieldCheckSql, Integer.class, Integer.parseInt(fieldId.trim()));
                if (fieldCount == null || fieldCount == 0) {
                    throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Field with ID " + fieldId + " does not exist");
                } else {
                    // Check if the provinces exist
                    String[] provinceIdsArray = jobDtoIn.getProvinceIds().split("-");
                    for (String provinceId : provinceIdsArray) {
                        String provinceCheckSql = "SELECT COUNT(*) FROM job_province WHERE id = ?";
                        Integer provinceCount = jdbcTemplate.queryForObject(provinceCheckSql, Integer.class, Integer.parseInt(provinceId.trim()));
                        if (provinceCount == null || provinceCount == 0) {
                            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province with ID " + provinceId + " does not exist");
                        }
                    }
                }
            }
        }
    }
}
