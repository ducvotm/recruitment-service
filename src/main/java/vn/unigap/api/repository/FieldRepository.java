package vn.unigap.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.Field;
import vn.unigap.api.entity.Job;
import vn.unigap.api.entity.Province;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {
    Optional<Field> findById(Long id);
}
