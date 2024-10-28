package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.jpa.Job;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDtoOut {

    @JsonProperty
    private Long id;

    @JsonProperty
    private Long employerId;

    @JsonProperty
    private String title;

    @JsonProperty
    private Integer quantity;

    @JsonProperty
    private String description;

    @JsonProperty
    private Integer salary;

    @JsonProperty
    private String fields;

    @JsonProperty
    private String provinces;

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime updatedAt;

    @JsonProperty
    private LocalDateTime expiredAt;

    public static JobDtoOut from(Job job) {
        return JobDtoOut.builder().id(job.getId()).employerId(job.getEmployerId()).title(job.getTitle())
                .quantity(job.getQuantity()).description(job.getDescription()).salary(job.getSalary())
                .fields(job.getFields()).provinces(job.getProvinces()).createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt()).expiredAt(job.getExpiredAt()).build();
    }
}
