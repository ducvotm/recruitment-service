package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobWithSeekersDtoOut {
    @JsonProperty
    private Long id;

    @JsonProperty
    private String title;

    @JsonProperty
    private Integer quantity;

    @JsonProperty
    private List<FieldDtoOut> fields;

    @JsonProperty
    private List<ProvinceDtoOut> provinces;

    @JsonProperty
    private Integer salary;

    @JsonProperty
    private LocalDateTime expiredAt;

    @JsonProperty
    private Long employerId;

    @JsonProperty
    private String employerName;

    @JsonProperty
    private List<SeekerDtoOut> seekers;
}
