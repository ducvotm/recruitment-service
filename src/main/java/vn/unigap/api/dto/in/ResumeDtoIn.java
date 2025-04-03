package vn.unigap.api.dto.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDtoIn {

    @NotEmpty
    private long seekerId;

    @NotEmpty
    private String careerObj;

    @NotEmpty
    private String title;

    @NotEmpty
    private int salary;

    @NotEmpty
    private String fieldIds;

    @NotEmpty
    private String provinceIds;
}
