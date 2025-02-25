package com.wiftwift.repository;

import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.ExpenseParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, ExpenseParticipantId> {
   Optional<ExpenseParticipant> findById(ExpenseParticipantId id);
   List<ExpenseParticipant> findAllById_ExpenseId(Long id);
   List<ExpenseParticipant> findByExpenseId(Long id);
}
