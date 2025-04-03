package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.jpa.Resume;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDtoOut {

    @JsonProperty
    private long id;

    @JsonProperty
    private long seekerId;

    @JsonProperty
    private String careerObj;

    @JsonProperty
    private String title;

    @JsonProperty
    private int salary;

    @JsonProperty
    private String fields;

    @JsonProperty
    private String provinces;

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime updatedAt;

    public static ResumeDtoOut from(Resume resume) {
        return ResumeDtoOut.builder().id(resume.getId()).seekerId(resume.getSeekerId()).careerObj(resume.getCareerObj())
                .title(resume.getTitle()).salary(resume.getSalary()).fields(resume.getFields())
                .provinces(resume.getProvinces()).createdAt(resume.getCreatedAt()).updatedAt(resume.getUpdatedAt())
                .build();
    }
}
