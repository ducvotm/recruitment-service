package vn.unigap.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.unigap.api.entity.Employer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByEmail(String email);
    Page<Employer> findAll(Pageable pageable);

    @Query("SELECT DATE(e.createdAt), COUNT(e.id) FROM Employer e WHERE e.createdAt between :startOfDay and :endOfDay GROUP BY DATE(e.createdAt)")
    List<Object[]> findEmployerCountForDate(@Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}