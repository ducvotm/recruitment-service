package vn.unigap.api.dto.in;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class MetricsByDateDtoIn {

    @NotNull
    private LocalDate fromDate;

    @NotNull
    private LocalDate toDate;
}
