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
    public JobDtoOut create(JobDtoIn jobDtoIn) {
        validateJobReferences(jobDtoIn);

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
    public void delete(Long id) {
        Job job = findJob(id);

        jobRepository.delete(job);
    }

    @Override
    public JobWithSeekersDtoOut getJobWithMatchingSeekers(Long id) {
        Job job = findJob(id);

        List<Resume> resumesBySalary = resumeRepository.findResumesBySalary(job.getSalary());

        Set<String> jobFieldSet = Arrays.stream(job.getFields().split("-"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        Set<String> jobProvinceSet = Arrays.stream(job.getProvinces().split("-"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

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

        Employer employer = employerRepository.findById(job.getEmployerId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Employer not found"));

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
