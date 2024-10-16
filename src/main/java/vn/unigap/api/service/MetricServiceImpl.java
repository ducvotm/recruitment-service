package vn.unigap.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;
import vn.unigap.api.dto.out.ChartDtoOut;
import vn.unigap.api.repository.EmployerRepository;
import vn.unigap.api.repository.JobRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricServiceImpl implements MetricService {

    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MetricServiceImpl(EmployerRepository employerRepository, JobRepository jobRepository,
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.employerRepository = employerRepository;
        this.jobRepository = jobRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public MetricsByDateDtoOut getMetricsByDate(MetricsByDateDtoIn metricsByDateDtoIn) {
        LocalDate fromDate = metricsByDateDtoIn.getFromDate();
        LocalDate toDate = metricsByDateDtoIn.getToDate();
        String cacheKey = "metrics:" + fromDate + " to " + toDate;

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

        // If not in cache or parsing failed, calculate metrics
        LocalDateTime startDay = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime endDay = LocalDateTime.of(toDate, LocalTime.MAX);

        // Initialize the chart data and counters
        List<ChartDtoOut> chart = new ArrayList<>();
        Integer totalEmployers = 0;
        Integer totalJobs = 0;

        // Fetch employer and job counts grouped by date
        List<Object[]> employerResults = employerRepository.findEmployerCountForDate(startDay, endDay);
        List<Object[]> jobResults = jobRepository.findJobCountForDate(startDay, endDay);

        // Process the results by iterating through the date range
        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            LocalDate finalDate = date;

            // Find the daily employer count
            Integer dailyEmployerCount = employerResults.stream()
                    .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue()).findFirst().orElse(0);

            // Find the daily job count
            Integer dailyJobCount = jobResults.stream()
                    .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue()).findFirst().orElse(0);

            // Accumulate totals
            totalEmployers += dailyEmployerCount;
            totalJobs += dailyJobCount;

            // Add data to the chart
            chart.add(new ChartDtoOut(date, dailyEmployerCount, dailyJobCount));
        }

        // Create MetricsByDateDtoOut
        MetricsByDateDtoOut metricsData = new MetricsByDateDtoOut(totalEmployers, totalJobs, chart);

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
