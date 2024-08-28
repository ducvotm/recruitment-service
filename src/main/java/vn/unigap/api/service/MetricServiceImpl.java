package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
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
public class MetricServiceImpl implements MetricService{

    private final EmployerRepository employerRepository;
    private final JobRepository jobRepository;

    @Autowired MetricServiceImpl(EmployerRepository employerRepository, JobRepository jobRepository) {
        this.employerRepository = employerRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public MetricsByDateDtoOut getMetricsByDate(MetricsByDateDtoIn metricsByDateDtoIn) {
        LocalDate fromDate = metricsByDateDtoIn.getFromDate();
        LocalDate toDate = metricsByDateDtoIn.getToDate();

        List<ChartDtoOut> chart = new ArrayList<>();

        Integer totalEmployers = 0;
        Integer totalJobs = 0;

        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);

            Integer dailyEmployerCount = this.employerRepository.findEmployerCountForDate(startOfDay, endOfDay);
            Integer dailyJobCount = this.jobRepository.findJobCountForDate(startOfDay, endOfDay);

            totalEmployers += dailyEmployerCount;
            totalJobs += dailyJobCount;

            chart.add(new ChartDtoOut(date, dailyEmployerCount, dailyJobCount));
        }

        return new MetricsByDateDtoOut(totalEmployers, totalJobs, chart);
    }

}
