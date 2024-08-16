package vn.unigap.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import vn.unigap.api.common.ApiException;
import vn.unigap.api.common.ErrorCode;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.entity.Job;
import vn.unigap.api.repository.JobRepository;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ModelMapper modelMapper;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, JdbcTemplate jdbcTemplate, ModelMapper modelMapper) {
        this.jobRepository = jobRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.modelMapper = modelMapper;
    }

    @Override
    public JobDtoOut create(JobDtoIn jobDtoIn) {
        // Check if the employer exists
        String employerCheckSql = "SELECT COUNT(*) FROM employers WHERE id = ?";
        Integer employerCount = jdbcTemplate.queryForObject(employerCheckSql, Integer.class, jobDtoIn.getEmployerId());
        if (employerCount == null || employerCount == 0) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Employer does not exist");
        }

        // Check if the fields exist
        String fieldCheckSql = "SELECT COUNT(*) FROM job_field WHERE id = ?";
        Integer fieldCount = jdbcTemplate.queryForObject(fieldCheckSql, Integer.class, jobDtoIn.getFieldIds());
        if (fieldCount == null || fieldCount == 0) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Field does not exist");
        }

        // Check if the provinces exist
        String provinceCheckSql = "SELECT COUNT(*) FROM job_province WHERE id = ?";
        Integer provinceCount = jdbcTemplate.queryForObject(fieldCheckSql, Integer.class, jobDtoIn.getProvinceIds());
        if (provinceCount == null || provinceCount == 0) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province does not exist");
        }

        // Convert DTO to Entity
        Job job = modelMapper.map(jobDtoIn, Job.class);

        // Save the created job
        job = jobRepository.save(job);

        // Convert Entity to DTO
        return modelMapper.map(job, JobDtoOut.class);
    }
}
