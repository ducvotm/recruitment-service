package vn.unigap.api.entity;

import jakarta.persistence.*;

import lombok.*;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String email;

    private String name;
    private String provinceId;
    private String description;
    private Date created_at = new Date();
    private Date updated_at = new Date();
}
