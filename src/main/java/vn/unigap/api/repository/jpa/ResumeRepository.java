package vn.unigap.api.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.jpa.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
