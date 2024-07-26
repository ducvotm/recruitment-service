package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.unigap.api.dto.in.EmployerDtoIn;
import vn.unigap.api.dto.out.EmployerDtoOut;
import vn.unigap.api.entity.Employer;
import vn.unigap.api.repository.EmployerRepository;

@Service
public class EmployerServiceImpl {

    private final EmployerRepository employerRepository;

    @Autowired
    public EmployerServiceImpl(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    public EmployerDtoOut create(EmployerDtoIn employerDtoIn) {

        Employer employer = employerRepository.save(Employer.builder()
                .email(employerDtoIn.getEmail())
                .name(employerDtoIn.getName())
                .provinceId(employerDtoIn.getProvinceId())
                .description(employerDtoIn.getDescription())
                .build());

        // Sử dụng phương thức from để chuyển đổi entity sang DTO
        return EmployerDtoOut.from(employer);
    }
}
