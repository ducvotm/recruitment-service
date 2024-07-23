package vn.unigap.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.unigap.api.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
