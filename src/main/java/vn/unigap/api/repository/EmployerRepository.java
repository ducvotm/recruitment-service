package vn.unigap.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.unigap.api.entity.Employer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByEmail(String email);
    Page<Employer> findAll(Pageable pageable);

    @Query("SELECT COUNT(id) FROM Employer e WHERE e.created_at between :from and :to")
    Optional<Employer> findTotalEmployerByDate(@Param("from") LocalDate from, @Param("to") LocalDate to);
}