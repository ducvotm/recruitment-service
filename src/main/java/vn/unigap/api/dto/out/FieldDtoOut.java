package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldDtoOut {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;
}
