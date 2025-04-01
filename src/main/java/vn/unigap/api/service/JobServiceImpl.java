package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.out.*;
import vn.unigap.api.entity.jpa.Resume;
import vn.unigap.api.entity.jpa.Seeker;
import vn.unigap.api.repository.jpa.*;
import vn.unigap.common.exception.ApiException;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.entity.jpa.Job;
import vn.unigap.api.entity.jpa.Employer;
import vn.unigap.common.utils.ValidationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final FieldRepository fieldRepository;
    private final ProvinceRepository provinceRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ResumeRepository resumeRepository;
    private final SeekerRepository seekerRepository;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, EmployerRepository employerRepository,
                          FieldRepository fieldRepository, ProvinceRepository provinceRepository, JdbcTemplate jdbcTemplate, ResumeRepository resumeRepository, SeekerRepository seekerRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
        this.fieldRepository = fieldRepository;
        this.provinceRepository = provinceRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.resumeRepository = resumeRepository;
        this.seekerRepository = seekerRepository;
    }

    @Override
    @CacheEvict(value = "JOBS", allEntries = true)
    public JobDtoOut create(JobDtoIn jobDtoIn) { validateJobReferences(jobDtoIn);
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

        return JobDtoOut.from(job);

    }

    @Override
    @CachePut(value = "JOB", key = "#id")
    public JobDtoOut update(Long id, JobDtoIn jobDtoIn) {
        validateJobReferences(jobDtoIn);

        Job existingJob = findJob(id);

        existingJob.setTitle(jobDtoIn.getTitle());
        existingJob.setQuantity(jobDtoIn.getQuantity());
        existingJob.setDescription(jobDtoIn.getDescription());
        existingJob.setFields(jobDtoIn.getFieldIds());
        existingJob.setProvinces(jobDtoIn.getProvinceIds());
        existingJob.setSalary(jobDtoIn.getSalary());
        existingJob.setExpiredAt(jobDtoIn.getExpiredAt());
        existingJob.setEmployerId(jobDtoIn.getEmployerId());

        Job updatedJob = jobRepository.save(existingJob);

        return JobDtoOut.from(updatedJob);
    }

    @Override
    @Cacheable(value = "JOB", key = "#id")
    public JobDtoOut get(Long id) {
        Job job = findJob(id);

        return JobDtoOut.from(job);
    }

    @Override
    @Cacheable(value = "JOBS", key = "#pageDtoIn")
    public PageDtoOut<JobDtoOut> list(PageDtoIn pageDtoIn) {

        Page<Job> jobs = this.jobRepository.findAllJobsOrderedByExpiredAtAndEmployerName(
                PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getPageSize()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), jobs.getTotalElements(),
                jobs.stream().map(JobDtoOut::from).toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "JOB", key = "#id"),
            @CacheEvict(value = "JOBS", allEntries = true)
    })
    public void delete(Long id) {
        Job job = findJob(id);

        jobRepository.delete(job);
    }

    private Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));
    }

    private void validateJobReferences(JobDtoIn jobDtoIn) {
        ValidationUtils.validateIdExists(jobDtoIn.getEmployerId(), employerRepository,"employer");
        ValidationUtils.validateIdsExist(jobDtoIn.getFieldIds(), fieldRepository,"field");
        ValidationUtils.validateIdsExist(jobDtoIn.getProvinceIds(), provinceRepository,"province");
    }

}
