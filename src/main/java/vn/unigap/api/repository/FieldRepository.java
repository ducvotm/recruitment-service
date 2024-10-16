package vn.unigap.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.Field;

public interface FieldRepository extends JpaRepository<Field, Long> {
}
