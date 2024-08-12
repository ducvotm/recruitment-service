package vn.unigap.api.common;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.unigap.api.dto.in.JobDtoIn;
import vn.unigap.api.dto.out.JobDtoOut;
import vn.unigap.api.entity.Job;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom mappings for JobDtoIn to Job
        modelMapper.addMappings(new PropertyMap<JobDtoIn, Job>() {
            @Override
            protected void configure() {
                map().setEmployerId(source.getEmployerId());  // Map Long directly
                map().setFields(String.valueOf(source.getFieldIds()));  // Convert Long to String if needed
                map().setProvinces(Math.toIntExact(source.getProvinceIds()));  // Convert Long to Integer
                map().setExpiredAt(source.getExpiredAt());
            }
        });

        // Custom mappings for Job to JobDtoOut
        modelMapper.addMappings(new PropertyMap<Job, JobDtoOut>() {
            @Override
            protected void configure() {
                // Mapping fields directly if names match
                map().setFields(source.getFields());  // No conversion needed here
                map().setProvinces(source.getProvinces());  // No conversion needed here
            }
        });

        return new ModelMapper();
    }
}
