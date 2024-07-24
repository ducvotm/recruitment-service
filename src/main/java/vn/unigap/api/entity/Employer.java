package vn.unigap.api.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employer {
    @Id
    private long id;

    @Column(unique = true)
    private String email;

    private String name;
    private int province;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
