package vn.unigap.api.service;

import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;

public interface JobService {
    JobDtoOut create(JobDtoIn jobDtoIn);

    JobDtoOut update(Long jobId, JobDtoIn jobDtoIn);

    JobDtoOut get(Long id);

    PageDtoOut<JobDtoOut> list(PageDtoIn pageDtoIn);

    void delete(Long id);
}
