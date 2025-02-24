package com.wiftwift.repository;

import com.wiftwift.model.Group;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findById(Long id);

    @Query(value = "SELECT * FROM find_user_for_payment(?1)", nativeQuery = true)
    Object[] findUserForPayment(Long groupId);
}
