package vn.unigap.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.unigap.api.entity.jpa.Seeker;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeekerDtoOut {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String birthday;

    @JsonProperty
    private String address;

    @JsonProperty
    private Long provinceId;

    @JsonProperty
    private String provinceName;

    public static SeekerDtoOut from(Seeker seeker) {
        return SeekerDtoOut.builder().id(seeker.getId()).name(seeker.getName()).birthday(seeker.getBirthday())
                .address(seeker.getAddress()).provinceId(seeker.getProvince())
                .provinceName(seeker.getProvinceTable() != null ? seeker.getProvinceTable().getName() : null).build();
    }
}
