package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    public JobDtoOut create(JobDtoIn jobDtoIn) {

        validateEmployerFieldAndProvinceExistence(jobDtoIn);

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
    public JobDtoOut update(Long id, JobDtoIn jobDtoIn) {

        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Job not found"));

        validateEmployerFieldAndProvinceExistence(jobDtoIn);

        Job updatedjob = jobRepository
                .save(Job.builder()
                        .title(jobDtoIn.getTitle())
                        .quantity(jobDtoIn.getQuantity())
                        .description(jobDtoIn.getDescription())
                        .fields(jobDtoIn.getFieldIds())
                        .provinces(jobDtoIn.getProvinceIds())
                        .salary(jobDtoIn.getSalary())
                        .expiredAt(jobDtoIn.getExpiredAt()).build());

        // Convert updated entity to DTO
        return JobDtoOut.from(updatedjob);
    }

    @Override
    @Cacheable(value = "JOB", key = "#id")
    public JobDtoOut get(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Job not found"));

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
    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "user not found"));
        jobRepository.delete(job);
    }

    @Override
    public JobWithSeekersDtoOut getJobWithMatchingSeekers(Long id) {
        // Retrieve the Job entity.
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Job not found"));

        // Retrieve resumes satisfying the salary condition.
        List<Resume> resumesBySalary = resumeRepository.findResumesBySalary(job.getSalary());

        // Build job's field and province sets from the dash-separated string.
        // Note: Fields and provinces are stored as strings like "1-3-5" etc.
        Set<String> jobFieldSet = Arrays.stream(job.getFields().split("-"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        Set<String> jobProvinceSet = Arrays.stream(job.getProvinces().split("-"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        // Filter resumes by checking that at least one field and one province match.
        List<Resume> matchingResumes = resumesBySalary.stream().filter(resume -> {
            Set<String> resumeFieldSet = Arrays.stream(resume.getFields().split("-"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            Set<String> resumeProvinceSet = Arrays.stream(resume.getProvinces().split("-"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            boolean fieldMatches = resumeFieldSet.stream().anyMatch(jobFieldSet::contains);
            boolean provinceMatches = resumeProvinceSet.stream().anyMatch(jobProvinceSet::contains);
            return fieldMatches && provinceMatches;
        }).collect(Collectors.toList());

        // Map resumes to a list of Seeker DTOs.
        // Use resume.getSeekerId() to fetch the corresponding Seeker.
        List<SeekerDtoOut> matchingSeekers = matchingResumes.stream()
                .map(resume -> {
                    Long seekerId = resume.getSeekerId(); // Use the seeker id from the resume entity.
                    Seeker seeker = seekerRepository.findById(seekerId)
                            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND,
                                    "Seeker with ID " + seekerId + " not found"));
                    return SeekerDtoOut.from(seeker);
                })
                .distinct()
                .collect(Collectors.toList());

        // Map job's fields to DTOs using the jobFieldSet.
        List<FieldDtoOut> fieldDtos = jobFieldSet.stream()
                .map(fieldIdStr -> {
                    Long fieldId = Long.valueOf(fieldIdStr);
                    var fieldEntity = fieldRepository.findById(fieldId)
                            .orElseThrow(() -> new ApiException(
                                    ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND,
                                    "Field with ID " + fieldId + " not found"));
                    return new FieldDtoOut(fieldEntity.getId(), fieldEntity.getName());
                })
                .collect(Collectors.toList());

        // Map job's provinces to DTOs using the jobProvinceSet.
        List<ProvinceDtoOut> provinceDtos = jobProvinceSet.stream()
                .map(provinceIdStr -> {
                    Long provinceId = Long.valueOf(provinceIdStr);
                    var provinceEntity = provinceRepository.findById(provinceId)
                            .orElseThrow(() -> new ApiException(
                                    ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND,
                                    "Province with ID " + provinceId + " not found"));
                    return new ProvinceDtoOut(provinceEntity.getId(), provinceEntity.getName());
                })
                .collect(Collectors.toList());

        // Fetch the Employer entity using the job's employerId.
        Employer employer = employerRepository.findById(job.getEmployerId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Employer not found"));

        // Build and return the JobWithSeekersDtoOut object with the desired fields.
        return new JobWithSeekersDtoOut(
                job.getId(),
                job.getTitle(),
                job.getQuantity(),
                fieldDtos,
                provinceDtos,
                job.getSalary(),
                job.getExpiredAt(),
                employer.getId(),
                employer.getName(),
                matchingSeekers
        );
    }


    private void validateEntityExists(Long id, JpaRepository<?, Long> repository, String entityName) {
        if(!repository.existsById(id)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "The " + entityName + "does not exist");
        }
    }

    private void validateIdsExist(String idsString, JpaRepository<?, Long> repository, String entityName) {

        List<Long> ids = Arrays.stream(idsString.split("-"))
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<?> existingEntities = repository.findAllById(id);

        if
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

        // Check if the provinces exist
        String[] provinceIdsArray = jobDtoIn.getProvinceIds().split("-");
        for (String provinceId : provinceIdsArray) {
            if (!provinceRepository.existsById(Long.valueOf(provinceId))) {
                throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST,
                        "The province with ID " + provinceId + " does not exist");
            }
        }
    }
}
