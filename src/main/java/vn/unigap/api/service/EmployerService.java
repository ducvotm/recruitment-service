package vn.unigap.api.service;

import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.dto.out.UpdateEmployerDtoOut;

public interface EmployerService {
    EmployerDtoOut create(EmployerDtoIn employerDtoIn);
    UpdateEmployerDtoOut update(Long id, EmployerDtoIn employerDtoIn);
    EmployerDtoOut get(Long id);
    PageDtoOut<EmployerDtoOut> list(PageDtoIn pageDtoIn);
    void delete(Long id);
}
