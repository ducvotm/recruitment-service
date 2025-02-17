package vn.unigap.api.repository.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.unigap.api.entity.jpa.Seeker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeekerRepository extends JpaRepository<Seeker, Long> {

    @Query("SELECT s FROM Seeker s LEFT JOIN FETCH s.provinceTable WHERE s.id = :id")
    Optional<Seeker> findByIdWithProvince(@Param("id") Integer id);

    @Query("SELECT DATE(s.createdAt), COUNT(s.id) FROM Seeker s WHERE s.createdAt between :start and :end GROUP BY DATE(s.createdAt)")
    List<Object[]> countSeekerByDate(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}
