package vn.unigap.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.in.SeekerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.in.UpdateSeekerDtoIn;
import vn.unigap.api.dto.out.SeekerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.entity.jpa.Seeker;
import vn.unigap.api.entity.jpa.Province;
import vn.unigap.api.repository.jpa.JobRepository;
import vn.unigap.api.repository.jpa.SeekerRepository;
import vn.unigap.api.repository.jpa.ProvinceRepository;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;

import vn.unigap.api.dto.out.PageDtoOut;
import org.springframework.data.domain.PageImpl;
import vn.unigap.common.utils.ValidationUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Slf4j
@Service
public class SeekerServiceImpl implements SeekerService {

    private final SeekerRepository seekerRepository;
    private final ProvinceRepository provinceRepository;
    private final JobRepository jobRepository;

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;


    @Autowired
    public SeekerServiceImpl(SeekerRepository seekerRepository,
                             ProvinceRepository provinceRepository,
                             RedisTemplate<String, Object> redisTemplate,
                             ObjectMapper objectMapper, JobRepository jobRepository) {
        this.seekerRepository = seekerRepository;
        this.provinceRepository = provinceRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.jobRepository = jobRepository;
    }

    @Override
    public SeekerDtoOut create(SeekerDtoIn seekerDtoIn) {
        ValidationUtils.validateIdExists(seekerDtoIn.getProvinceId(), provinceRepository, "province");

        Seeker seeker = Seeker.builder()
                .name(seekerDtoIn.getName())
                .birthday(seekerDtoIn.getBirthday())
                .address(seekerDtoIn.getAddress())
                .province(seekerDtoIn.getProvinceId())
                .build();

        seeker = seekerRepository.save(seeker);

        return SeekerDtoOut.from(seeker);
    }

    @Override
    public SeekerDtoOut update(Long id, UpdateSeekerDtoIn updateSeekerDtoIn) {
        ValidationUtils.validateIdExists(updateSeekerDtoIn.getProvinceId(), provinceRepository, "province");

        Seeker seeker = findSeeker(id);

        seeker.setName(updateSeekerDtoIn.getName());
        seeker.setBirthday(updateSeekerDtoIn.getBirthday());
        seeker.setAddress(updateSeekerDtoIn.getAddress());
        seeker.setProvince(updateSeekerDtoIn.getProvinceId());

        seeker = seekerRepository.save(seeker);

        return SeekerDtoOut.from(seeker);
    }

    @Override
    @Cacheable(value = "SEEKER", key = "#id")
    public SeekerDtoOut get(Long id) {
        Seeker seeker = findSeeker(id);

        return SeekerDtoOut.from(seeker);
    }

    @Override
    @Cacheable(value = "SEEKERS", key = "#pageDtoIn")
    public PageDtoOut<SeekerDtoOut> list(PageDtoIn pageDtoIn) {
        Page<Seeker> seekers = this.seekerRepository
                .findAll(PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getPageSize(), Sort.by("name").descending()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), seekers.getTotalElements(),
                seekers.stream()
                        .map(SeekerDtoOut::from)
                        .toList()
        );
    }


    @Override
    public void delete(Long id) {
        Seeker seeker = findSeeker(id);

        seekerRepository.delete(seeker);
    }

    private Seeker findSeeker(Long id) {
        return seekerRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND, "seeker not found"));
    }
}