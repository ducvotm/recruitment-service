package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;
import vn.unigap.api.repository.EmployerRepository;

import java.time.LocalDate;

@Service
public class MetricServiceImpl implements MetricService{

    private final EmployerRepository employerRepository;

    @Autowired MetricServiceImpl(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Override
    public MetricsByDateDtoOut getMetricsByDate(MetricsByDateDtoIn metricsByDateDtoIn) {

        employerRepository.findTotalEmployerByDate(metricsByDateDtoIn.getFromDate(), metricsByDateDtoIn.getToDate());

        return null;
    }

}
