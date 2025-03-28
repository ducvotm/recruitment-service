package vn.unigap.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDailyCount {
    private String entityName;
    private List<Object[]> countResults;
    private Integer totalCount = 0;

    public Integer getDailyCount(LocalDate date) {
        Integer count = countResults.stream()
                .filter(r -> ((java.sql.Date) r[0]).toLocalDate().equals(date))
                .map(r -> ((Number) r[1]).intValue())
                .findFirst()
                .orElse(0);

        totalCount += count;
        return count;
    }
}
