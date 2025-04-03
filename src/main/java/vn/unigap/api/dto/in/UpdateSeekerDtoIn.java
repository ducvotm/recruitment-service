package vn.unigap.api.dto.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSeekerDtoIn {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String birthday;

    private String address;

    @NotNull
    private Long provinceId;
}
