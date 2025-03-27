package vn.unigap.api.entity.jpa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "seeker", indexes = {@Index(columnList = "name"), @Index(columnList = "created_at")})
public class Seeker {

    public static final String DATE_FORMAT = "yyyy-MM-dd"; // Predefined date format

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String name;

    @JsonFormat(pattern = DATE_FORMAT)
    private String birthday;

    private String address;

    @ManyToOne()
    @JoinColumn(name = "province")
    private Province provinceTable;

    @Column(name = "province", insertable = false, updatable = false)
    private Long province;

    @Column(name = "created_at", updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}