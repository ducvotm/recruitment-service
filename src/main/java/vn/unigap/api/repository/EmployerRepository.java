package vn.unigap.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.unigap.api.entity.Employer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByEmail(String email);
    Page<Employer> findAll(Pageable pageable);

    @Query("SELECT COUNT(e.id) FROM Employer e WHERE e.createdAt between :from and :to")
    Integer findTotalEmployerByDate(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}