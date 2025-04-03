package vn.unigap.api.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.unigap.api.entity.jpa.Employer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByEmail(String email);

    Page<Employer> findAll(Pageable pageable);

    @Query("SELECT DATE(e.createdAt), COUNT(e.id) FROM Employer e WHERE e.createdAt between :start and :end GROUP BY DATE(e.createdAt)")
    List<Object[]> countEmployerByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
