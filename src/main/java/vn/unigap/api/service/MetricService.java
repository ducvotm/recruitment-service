package vn.unigap.api.service;

import vn.unigap.api.dto.in.MetricsByDateDtoIn;
import vn.unigap.api.dto.out.MetricsByDateDtoOut;

public interface MetricService {
    MetricsByDateDtoOut getMetricsByDate(MetricsByDateDtoIn metricsByDateDtoIn);
}