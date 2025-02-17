package vn.unigap.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;
import vn.unigap.api.dto.out.ChartDtoOut;
import vn.unigap.api.repository.jpa.EmployerRepository;
import vn.unigap.api.repository.jpa.JobRepository;
import vn.unigap.api.repository.jpa.ResumeRepository;
import vn.unigap.api.repository.jpa.SeekerRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricServiceImpl implements MetricService {

    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final SeekerRepository seekerRepository;
    private final ResumeRepository resumeRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MetricServiceImpl(EmployerRepository employerRepository, JobRepository jobRepository, SeekerRepository seekerRepository, ResumeRepository resumeRepository,
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.employerRepository = employerRepository;
        this.jobRepository = jobRepository;
        this.seekerRepository = seekerRepository;
        this.resumeRepository = resumeRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public MetricsByDateDtoOut getMetricsByDate(MetricsByDateDtoIn metricsByDateDtoIn) {
        LocalDate from = metricsByDateDtoIn.getFromDate();
        LocalDate to = metricsByDateDtoIn.getToDate();
        String cacheKey = "metrics:" + from + " to " + to;

        // Try to get data from Redis cache first
        String cachedMetricsJson = redisTemplate.opsForValue().get(cacheKey);
        if (cachedMetricsJson != null) {
            try {
                return objectMapper.readValue(cachedMetricsJson, MetricsByDateDtoOut.class);
            } catch (JsonProcessingException e) {
                // Log the error, but continue to recalculate the metrics
                // logger.error("Error parsing cached metrics JSON", e);
            }
        }

        LocalDateTime fromDateTime = LocalDateTime.of(from, LocalTime.MIN);
        LocalDateTime toDateTime = LocalDateTime.of(to, LocalTime.MAX);

        // Initialize the chart data and counters
        List<ChartDtoOut> chart = new ArrayList<>();
        Integer totalEmployers = 0;
        Integer totalJobs = 0;
        Integer totalSeekers = 0;
        Integer totalResumes = 0;

        // Fetch employer and job counts grouped by date
        List<Object[]> employerResults = employerRepository.countEmployerByDate(fromDateTime, toDateTime);
        List<Object[]> jobResults = jobRepository.countJobByDate(fromDateTime, toDateTime);
        List<Object[]> seekerResults = seekerRepository.countSeekerByDate(fromDateTime, toDateTime);
        List<Object[]> resumeResults = resumeRepository.countResumeByDate(fromDateTime, toDateTime);


        // Process the results by iterating through the date range
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            LocalDate finalDate = date;

            // Find the daily employer count
            Integer dailyEmployerCount = employerResults.stream()
                    .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue()).findFirst().orElse(0);

            // Find the daily job count
            Integer dailyJobCount = jobResults.stream()
                    .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue()).findFirst().orElse(0);

            Integer dailySeekerCount = seekerResults.stream()
                    .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue()).findFirst().orElse(0);

            Integer dailyResumeCount = resumeResults.stream()
                    .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue()).findFirst().orElse(0);


            // Accumulate totals
            totalEmployers += dailyEmployerCount;
            totalJobs += dailyJobCount;
            totalSeekers += dailySeekerCount;
            totalResumes += dailyResumeCount;

            // Add data to the chart
            chart.add(new ChartDtoOut(date, dailyEmployerCount, dailyJobCount, dailySeekerCount, dailyResumeCount));
        }

        // Create MetricsByDateDtoOut
        MetricsByDateDtoOut metricsData = new MetricsByDateDtoOut(totalEmployers, totalJobs, totalSeekers, totalResumes, chart);

        // Save to Redis cache
        try {
            String json = objectMapper.writeValueAsString(metricsData);
            redisTemplate.opsForValue().set(cacheKey, json);
        } catch (JsonProcessingException e) {
            // Log the error, but don't throw an exception
            // logger.error("Error serializing metrics data to JSON", e);
        }

        return metricsData;
    }

}
