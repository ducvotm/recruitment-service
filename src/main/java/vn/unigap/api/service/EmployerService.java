package vn.unigap.api.service;

import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.out.EmployerDtoOut;

public interface EmployerService {
    EmployerDtoOut create(EmployerDtoIn employerDtoIn);
}
