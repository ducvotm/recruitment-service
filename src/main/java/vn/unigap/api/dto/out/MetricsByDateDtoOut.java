package vn.unigap.api.dto.out;

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
    private int numSeeker;
    private int numResume;
    private List<ChartDtoOut> chart;
}
