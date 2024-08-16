package vn.unigap.api.dto.out;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.Job;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "jobs")
public class JobDtoOut {

    private Long id;
    private Long employerId;
    private String title;
    private Integer quantity;
    private String description;
    private Integer salary;
    private String fields;
    private String provinces;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiredAt;

    public static JobDtoOut from(Job job) {
        return JobDtoOut.builder()
                .id(job.getId())
                .employerId(job.getEmployerId())
                .title(job.getTitle())
                .quantity(job.getQuantity())
                .description(job.getDescription())
                .salary(job.getSalary())
                .fields(job.getFields())
                .provinces(job.getProvinces())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .expiredAt(job.getExpiredAt())
                .build();
    }
}
