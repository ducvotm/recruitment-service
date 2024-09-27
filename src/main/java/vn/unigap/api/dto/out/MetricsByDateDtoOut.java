package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetricsByDateDtoOut {

    private int numEmployer;
    private int numJob;

    List<ChartDtoOut> chart;
}
