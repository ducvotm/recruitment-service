package vn.unigap.api.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.jpa.Field;

public interface FieldRepository extends JpaRepository<Field, Long> {
}
