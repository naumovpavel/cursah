package com.wiftwift.repository;
import com.wiftwift.model.Role;
import com.wiftwift.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String name);
    boolean existsByUsername(String username);
    @Modifying
    @Query("update User u set u.role = :role where u.id = :id")
    void updateRole(@Param("id") Long id, @Param("role") Role role);
}
