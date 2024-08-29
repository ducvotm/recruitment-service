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

        // Get date from the user
        LocalDate fromDate = metricsByDateDtoIn.getFromDate();
        LocalDate toDate = metricsByDateDtoIn.getToDate();

        // Define the start and the end of the Day
        LocalDateTime startDay = LocalDateTime.of(fromDate, LocalTime.MIN);
        LocalDateTime endDay = LocalDateTime.of(toDate, LocalTime.MAX);

        List<ChartDtoOut> chart = new ArrayList<>();

        Integer totalEmployers = 0;
        Integer totalJobs = 0;

        // Fetch employer counts grouped by date
        List<Object[]> employerResults = this.employerRepository.findEmployerCountForDate(startDay, endDay);
        // Fetch employer counts grouped by date
        List<Object[]> jobResults = this.jobRepository.findJobCountForDate(startDay, endDay);


        // Process the results
        for (LocalDate date = fromDate; !date.isAfter(toDate); date = date.plusDays(1)) {

            //Stream the results
            LocalDate finalDate = date;
            Integer dailyEmployerCount = employerResults.stream()
                    .filter(r -> r[0].equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue())
                    .findFirst()
                    .orElse(0);

            Integer dailyJobCount = jobResults.stream()
                    .filter(r -> r[0].equals(finalDate))
                    .map(r -> ((Number) r[1]).intValue())
                    .findFirst()
                    .orElse(0);

            totalEmployers += dailyEmployerCount;
            totalJobs += dailyJobCount;

            chart.add(new ChartDtoOut(date, dailyEmployerCount, dailyJobCount));
        }

        return new MetricsByDateDtoOut(totalEmployers, totalJobs, chart);
    }

}
