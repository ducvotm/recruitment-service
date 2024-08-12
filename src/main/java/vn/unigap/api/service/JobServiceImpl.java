package vn.unigap.api.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import vn.unigap.api.common.ApiException;
import vn.unigap.api.common.ErrorCode;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.entity.Field;
import vn.unigap.api.entity.Job;
import vn.unigap.api.entity.Province;
import vn.unigap.api.repository.FieldRepository;
import vn.unigap.api.repository.JobRepository;
import vn.unigap.api.repository.ProvinceRepository;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final ProvinceRepository provinceRepository;
    private final FieldRepository fieldRepository;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, ProvinceRepository provinceRepository, FieldRepository fieldRepository) {
        this.jobRepository = jobRepository;
        this.provinceRepository=  provinceRepository;
        this.fieldRepository = fieldRepository;
    }

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public JobDtoOut create(JobDtoIn jobDtoIn) {

        // Check if the employer is existing yet
        jobRepository.findById(jobDtoIn.getEmployerId()).ifPresent(user -> {
            throw new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Employer does not exist");
        });

        // Check if the field is existing yet
        Field field = fieldRepository.findById(jobDtoIn.getFieldIds())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Field does not exist"));

        // Check if the province exists
        Province jobProvince = provinceRepository.findById(jobDtoIn.getProvinceIds())
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST, "Province does not exist"));

        // Convert DTO to Entity
        Job job = modelMapper.map(jobDtoIn, Job.class);

        // Save the created job
        jobRepository.save(job);

        // Convert Entity to DTO
        return modelMapper.map(job, JobDtoOut.class);
    }
}
