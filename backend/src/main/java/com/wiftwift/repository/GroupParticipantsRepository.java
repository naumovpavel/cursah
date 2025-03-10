package com.wiftwift.repository;

import com.wiftwift.model.Group;
import com.wiftwift.model.GroupParticipants;
import com.wiftwift.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupParticipantsRepository extends JpaRepository<GroupParticipants, Long> {
    @Query("SELECT gr.user FROM GroupParticipants gr where gr.groupId = :groupId")
    List<User> findUserByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT gr.group FROM GroupParticipants gr where gr.userId = :userId")
    List<Group> findGroupByUserId(@Param("userId") Long userId);

    @Query("SELECT gr FROM GroupParticipants gr WHERE gr.userId = :userId AND gr.groupId = :groupId")
    Optional<GroupParticipants> GroupParticipantsByUserIdAndGroupId(@Param("userId") Long userId, @Param("groupId") Long groupId);
}
