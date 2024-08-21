package vn.unigap.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.Field;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {
}
