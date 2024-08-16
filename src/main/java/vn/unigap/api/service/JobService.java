package vn.unigap.api.service;

import jakarta.persistence.EntityNotFoundException;
import vn.unigap.api.common.ApiException;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;


public interface JobService {
    JobDtoOut create(JobDtoIn jobDtoIn);
}
