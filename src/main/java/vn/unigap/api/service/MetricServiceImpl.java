package vn.unigap.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.*;
import vn.unigap.api.entity.jpa.Employer;
import vn.unigap.api.entity.jpa.Job;
import vn.unigap.api.entity.jpa.Resume;
import vn.unigap.api.entity.jpa.Seeker;
import vn.unigap.api.repository.jpa.*;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;
import vn.unigap.common.utils.EntityDailyCount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MetricServiceImpl implements MetricService {

    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final SeekerRepository seekerRepository;
    private final ResumeRepository resumeRepository;
    private final FieldRepository fieldRepository;
    private final ProvinceRepository provinceRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MetricServiceImpl(EmployerRepository employerRepository, JobRepository jobRepository,
            SeekerRepository seekerRepository, ResumeRepository resumeRepository,
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, FieldRepository fieldRepository,
            ProvinceRepository provinceRepository) {
        this.employerRepository = employerRepository;
        this.jobRepository = jobRepository;
        this.seekerRepository = seekerRepository;
        this.resumeRepository = resumeRepository;
        this.fieldRepository = fieldRepository;
        this.provinceRepository = provinceRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Cacheable(value = "METRICS", key = "#metricsByDateDtoIn.fromDate + ' to ' + #metricsByDateDtoIn.toDate")
    public MetricsByDateDtoOut getMetricsByDate(MetricsByDateDtoIn metricsByDateDtoIn) {
        LocalDate from = metricsByDateDtoIn.getFromDate();
        LocalDate to = metricsByDateDtoIn.getToDate();

        validateDateRange(from, to);

        LocalDateTime fromDateTime = LocalDateTime.of(from, LocalTime.MIN);
        LocalDateTime toDateTime = LocalDateTime.of(to, LocalTime.MAX);

        List<EntityDailyCount> entityCount = fetchEntityCounts(fromDateTime, toDateTime);

        List<ChartDtoOut> chart = processDailyMetrics(from, to, entityCount);

        MetricsByDateDtoOut result = new MetricsByDateDtoOut(entityCount.get(0).getAccumulatedCount(),
                entityCount.get(1).getAccumulatedCount(), entityCount.get(2).getAccumulatedCount(),
                entityCount.get(3).getAccumulatedCount(), chart);

        return result;
    }

    @Override
    @Cacheable(value = "JOB_SEEKERS", key = "#id")
    public JobWithSeekersDtoOut getJobWithMatchingSeekers(Long id) {
        Job job = jobRepository.findById(id).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Job with ID " + id + " not found"));
        List<Resume> resumesBySalary = resumeRepository.findResumesBySalary(job.getSalary());

        Set<String> jobFieldSet = Arrays.stream(job.getFields().split("-")).filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        Set<String> jobProvinceSet = Arrays.stream(job.getProvinces().split("-")).filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<Resume> matchingResumes = resumesBySalary.stream().filter(resume -> {
            Set<String> resumeFieldSet = Arrays.stream(resume.getFields().split("-")).filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            Set<String> resumeProvinceSet = Arrays.stream(resume.getProvinces().split("-")).filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            boolean fieldMatches = resumeFieldSet.stream().anyMatch(jobFieldSet::contains);
            boolean provinceMatches = resumeProvinceSet.stream().anyMatch(jobProvinceSet::contains);
            return fieldMatches && provinceMatches;
        }).collect(Collectors.toList());

        List<SeekerDtoOut> matchingSeekers = matchingResumes.stream().map(resume -> {
            Long seekerId = resume.getSeekerId(); // Use the seeker id from the resume entity.
            Seeker seeker = seekerRepository.findById(seekerId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND,
                    HttpStatus.NOT_FOUND, "Seeker with ID " + seekerId + " not found"));
            return SeekerDtoOut.from(seeker);
        }).distinct().collect(Collectors.toList());

        List<FieldDtoOut> fieldDtos = jobFieldSet.stream().map(fieldIdStr -> {
            Long fieldId = Long.valueOf(fieldIdStr);
            var fieldEntity = fieldRepository.findById(fieldId).orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND,
                    HttpStatus.NOT_FOUND, "Field with ID " + fieldId + " not found"));
            return new FieldDtoOut(fieldEntity.getId(), fieldEntity.getName());
        }).collect(Collectors.toList());

        List<ProvinceDtoOut> provinceDtos = jobProvinceSet.stream().map(provinceIdStr -> {
            Long provinceId = Long.valueOf(provinceIdStr);
            var provinceEntity = provinceRepository.findById(provinceId)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND,
                            "Province with ID " + provinceId + " not found"));
            return new ProvinceDtoOut(provinceEntity.getId(), provinceEntity.getName());
        }).collect(Collectors.toList());

        Employer employer = employerRepository.findById(job.getEmployerId())
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "Employer not found"));

        return new JobWithSeekersDtoOut(job.getId(), job.getTitle(), job.getQuantity(), fieldDtos, provinceDtos,
                job.getSalary(), job.getExpiredAt(), employer.getId(), employer.getName(), matchingSeekers);
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From date and to date cannot be null");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before or equal to to date");
        }
    }

    private List<EntityDailyCount> fetchEntityCounts(LocalDateTime from, LocalDateTime to) {
        List<EntityDailyCount> entityCounts = new ArrayList<>();

        entityCounts.add(new EntityDailyCount("employer", employerRepository.countEmployerByDate(from, to), 0));

        entityCounts.add(new EntityDailyCount("job", jobRepository.countJobByDate(from, to), 0));

        entityCounts.add(new EntityDailyCount("seeker", seekerRepository.countSeekerByDate(from, to), 0));

        entityCounts.add(new EntityDailyCount("resume", resumeRepository.countResumeByDate(from, to), 0));

        return entityCounts;
    }

    private List<ChartDtoOut> processDailyMetrics(LocalDate from, LocalDate to, List<EntityDailyCount> entityCounts) {
        List<ChartDtoOut> chart = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            LocalDate currentDate = date;

            Integer employerCount = entityCounts.get(0).getDailyCount(currentDate);
            Integer jobCount = entityCounts.get(1).getDailyCount(currentDate);
            Integer seekerCount = entityCounts.get(2).getDailyCount(currentDate);
            Integer resumeCount = entityCounts.get(3).getDailyCount(currentDate);

            chart.add(new ChartDtoOut(currentDate, employerCount, jobCount, seekerCount, resumeCount));
        }

        return chart;
    }
}
