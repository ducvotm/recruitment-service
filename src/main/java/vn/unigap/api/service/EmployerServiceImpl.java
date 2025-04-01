package vn.unigap.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.in.PageDtoIn;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.dto.out.PageDtoOut;
import vn.unigap.api.dto.out.UpdateEmployerDtoOut;
import vn.unigap.api.entity.jpa.Employer;
import vn.unigap.api.entity.jpa.Province;
import vn.unigap.api.repository.jpa.EmployerRepository;
import vn.unigap.api.repository.jpa.ProvinceRepository;
import vn.unigap.common.errorcode.ErrorCode;
import vn.unigap.common.exception.ApiException;

@Service
public class EmployerServiceImpl implements EmployerService {

    private final EmployerRepository employerRepository;
    private final ProvinceRepository provinceRepository;

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;


    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository,
                               ProvinceRepository provinceRepository,
                               RedisTemplate<String, Object> redisTemplate,
                               ObjectMapper objectMapper) {
        this.employerRepository = employerRepository;
        this.provinceRepository = provinceRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @CacheEvict(value = "EMPLOYERS", allEntries = true)
    public EmployerDtoOut create(EmployerDtoIn employerDtoIn) {

        employerRepository.findByEmail(employerDtoIn.getEmail()).ifPresent(user -> {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Email already exists");
        });

        Province jobProvince = provinceRepository.findById(Long.valueOf(employerDtoIn.getProvince())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province does not exist"));

        Employer employer = Employer.builder()
                .email(employerDtoIn.getEmail())
                .name(employerDtoIn.getName())
                .province(employerDtoIn.getProvince())
                .description(employerDtoIn.getDescription())
                .build();

        employer = employerRepository.save(employer);

        return EmployerDtoOut.from(employer);
    }

    @Override
    @CachePut(value ="EMPLOYER", key = "#id")
    public UpdateEmployerDtoOut update(Long id, EmployerDtoIn employerDtoIn) {

        Employer employer = findEmployer(id);

        Province jobProvince = provinceRepository.findById(Long.valueOf(employerDtoIn.getProvince())).orElseThrow(
                () -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province does not exist"));

        employer.setEmail(employerDtoIn.getEmail());
        employer.setName(employerDtoIn.getName());
        employer.setProvince(employerDtoIn.getProvince());
        employer.setDescription(employerDtoIn.getDescription());

        employer = employerRepository.save(employer);

        return UpdateEmployerDtoOut.from(employer);
    }

    @Override
    @Cacheable(value = "EMPLOYER", key = "#id")
    public EmployerDtoOut get(Long id) {

        Employer employer = findEmployer(id);

        return EmployerDtoOut.from(employer);
    }

    @Override
    @Cacheable(value = "EMPLOYERS", key = "#pageDtoIn")
    public PageDtoOut<EmployerDtoOut> list(PageDtoIn pageDtoIn) {

        Page<Employer> employers = this.employerRepository
                .findAll(PageRequest.of(pageDtoIn.getPage() - 1, pageDtoIn.getPageSize(), Sort.by("name").descending()));

        return PageDtoOut.from(pageDtoIn.getPage(), pageDtoIn.getPageSize(), employers.getTotalElements(),
                employers.stream().map(EmployerDtoOut::from).toList());
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "EMPLOYER", key = "#id"),
            @CacheEvict(value = "EMPLOYERS", allEntries = true)
    })
    public void delete(Long id) {

        Employer employer = findEmployer(id);

        employerRepository.delete(employer);
    }

    private Employer findEmployer(Long id) {
        return employerRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.NOT_FOUND,
                        HttpStatus.NOT_FOUND,
                        "Employer not found with id: " + id
                ));
    }
}