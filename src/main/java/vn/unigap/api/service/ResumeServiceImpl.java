package vn.unigap.api.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.in.ResumeDtoIn;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.dto.out.ResumeDtoOut;
import vn.unigap.api.entity.jpa.Field;
import vn.unigap.api.entity.jpa.Province;
import vn.unigap.api.entity.jpa.Resume;
import vn.unigap.api.entity.jpa.Seeker;
import vn.unigap.api.repository.jpa.FieldRepository;
import vn.unigap.api.repository.jpa.ProvinceRepository;
import vn.unigap.api.repository.jpa.ResumeRepository;
import vn.unigap.api.repository.jpa.SeekerRepository;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final SeekerRepository seekerRepository;
    private final FieldRepository fieldRepository;
    private final ProvinceRepository provinceRepository;

    public ResumeServiceImpl(ResumeRepository resumeRepository, SeekerRepository seekerRepository, FieldRepository fieldRepository, ProvinceRepository provinceRepository) {
       this.resumeRepository = resumeRepository;
       this.seekerRepository = seekerRepository;
       this.fieldRepository = fieldRepository;
       this.provinceRepository = provinceRepository;
    }

    @Override
    public ResumeDtoOut create(ResumeDtoIn resumeDtoIn) {
        Seeker seeker = seekerRepository.findById(Long.valueOf(resumeDtoIn.getSeekerId())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Seeker not found"));

        Field field = fieldRepository.findById(Long.valueOf(resumeDtoIn.getFieldIds())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Field not found"));

        Province province = provinceRepository.findById(Long.valueOf(resumeDtoIn.getProvinceIds())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province not found"));

        Resume resume = Resume.builder()
                .seekerId(resumeDtoIn.getSeekerId())
                .careerObj(resumeDtoIn.getCareerObj())
                .title(resumeDtoIn.getTitle())
                .salary(resumeDtoIn.getSalary())
                .fields(resumeDtoIn.getFieldIds())
                .provinces(resumeDtoIn.getProvinceIds())
                .build();

        resume = resumeRepository.save(resume);

        return ResumeDtoOut.from(resume);

    }

    @Override
    public ResumeDtoOut update(Long id, ResumeDtoIn resumeDtoIn) {

        Field field = fieldRepository.findById(Long.valueOf(resumeDtoIn.getFieldIds())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Field not found"));

        Province province = provinceRepository.findById(Long.valueOf(resumeDtoIn.getProvinceIds())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province not found"));

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(()-> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Resume not found"));

        resume.setCareerObj(resumeDtoIn.getCareerObj());
        resume.setTitle(resumeDtoIn.getTitle());
        resume.setSalary(resumeDtoIn.getSalary());
        resume.setFields(resumeDtoIn.getFieldIds());
        resume.setProvinces(resumeDtoIn.getProvinceIds());

        resume = resumeRepository.save(resume);

        return ResumeDtoOut.from(resume);
    }

    @Override
    @Cacheable(value = "RESUME", key = "#id")
    public ResumeDtoOut get(Long id) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Resume not found"));

        return ResumeDtoOut.from(resume);
    }

    @Override
    @Cacheable(value = "RESUMES", key = "#pageDtoIn")
    public PageDtoOut<ResumeDtoOut> list(PageDtoIn pageDtoIn) {
        Page<Resume> resumes = resumeRepository
                .findAll(PageRequest.of(pageDtoIn.getPage() -1, pageDtoIn.getPageSize()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), resumes.getTotalElements(),
                resumes.stream().map(ResumeDtoOut::from).toList());

    }

    @Override
    public void delete(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Resume not found"));

        resumeRepository.delete(resume);
    }


}
