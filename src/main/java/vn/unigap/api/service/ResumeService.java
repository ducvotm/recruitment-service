package vn.unigap.api.service;

import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.in.ResumeDtoIn;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.dto.out.ResumeDtoOut;
import vn.unigap.api.entity.jpa.Resume;

public interface ResumeService {

    ResumeDtoOut create(ResumeDtoIn resumeDtoIn);
    ResumeDtoOut update(Long id, ResumeDtoIn resumeDtoIn);
    ResumeDtoOut get(Long id);
    PageDtoOut<ResumeDtoOut> list(PageDtoIn pageDtoIn);
    void delete(Long id);

}
