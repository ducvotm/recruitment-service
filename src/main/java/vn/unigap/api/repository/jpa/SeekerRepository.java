package vn.unigap.api.repository.jpa;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.unigap.api.entity.jpa.Seeker;

import java.util.Optional;

public interface SeekerRepository extends JpaRepository<Seeker, Long> {

    @Query("SELECT s FROM Seeker s LEFT JOIN FETCH s.provinceTable WHERE s.id = :id")
    Optional<Seeker> findByIdWithProvince(@Param("id") Integer id);
}
