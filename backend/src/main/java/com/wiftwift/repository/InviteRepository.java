package com.wiftwift.repository;

import com.wiftwift.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    List<Invite> findByInvitedUser(String username);   
}
