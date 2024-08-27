package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;
import vn.unigap.api.repository.EmployerRepository;
import vn.unigap.api.repository.JobRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        LocalDateTime from = metricsByDateDtoIn.getFromDate();
        LocalDateTime to = metricsByDateDtoIn.getToDate();

        Integer numEmployer = this.employerRepository.findTotalEmployerByDate(from, to);
        Integer numJob = this.jobRepository.findTotalJobByDate(from, to);

        return new MetricsByDateDtoOut(numEmployer, numJob);
    }

}
