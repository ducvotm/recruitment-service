package vn.unigap.api.service;

import vn.unigap.api.dto.in.SeekerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.in.UpdateSeekerDtoIn;
import vn.unigap.api.dto.out.SeekerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;

public interface SeekerService {
    SeekerDtoOut create(SeekerDtoIn seekerDtoIn);

    SeekerDtoOut update(Long id, UpdateSeekerDtoIn updateSeekerDtoIn);

    SeekerDtoOut get(Long id);

    PageDtoOut<SeekerDtoOut> list(PageDtoIn pageDtoIn);

    void delete(Long id);
}