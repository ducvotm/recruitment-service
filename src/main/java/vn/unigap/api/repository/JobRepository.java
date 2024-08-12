package vn.unigap.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.Job;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findById(Long employerId);
    Page<Job> findAll(Pageable pageable);
}
