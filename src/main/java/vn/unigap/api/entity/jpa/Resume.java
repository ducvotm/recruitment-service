package vn.unigap.api.entity.jpa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import static java.time.LocalTime.now;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "resume")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    private long seekerId;

    @Column(name = "career_obj", length = 5000)
    private String careerObj;

    private String title;
    private int salary;
    private String fields;
    private String provinces;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}
