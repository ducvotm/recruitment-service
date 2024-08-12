package vn.unigap.api.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobDtoIn {

    @NotNull
    private String title;

    @NotNull
    private Long employerId;

    @NotNull
    private Integer quantity;

    @NotNull
    private String description;

    @NotNull
    private Long fieldIds;

    @NotNull
    private Long provinceIds;

    @NotNull
    private Integer salary;

    @NotNull
    private LocalDateTime expiredAt;

}
