package com.wiftwift.service;

import com.wiftwift.model.Group;
import com.wiftwift.model.Invite;
import com.wiftwift.model.Role;
import com.wiftwift.model.User;
import com.wiftwift.repository.GroupParticipantsRepository;
import com.wiftwift.repository.InviteRepository;
import com.wiftwift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private GroupService groupService;
    @Autowired
    private GroupParticipantsRepository groupParticipantsRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User with this username already exists.");
        }
        user = save(user);
        return user;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found."));

    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public String getCurrentUserUsername() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    public User getCurrentUser() {
        return getByUsername(getCurrentUserUsername());
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found.")
        );
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public void setRole(Long id, Role role) {
        userRepository.updateRole(id, role);

    }

    public void removeAdmin(Long id) {
        userRepository.updateRole(id, Role.ROLE_USER);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public List<Invite> getMyInvites() {
        return inviteRepository.findByInvitedUser(getCurrentUserUsername());
    }

    public void approveInvite(Long inviteId) {
        Invite invite = inviteRepository.findById(inviteId).orElseThrow(
                () -> new RuntimeException("Invite not found.")
        );
        groupService.addParticipant(invite.getGroupId(), invite.getInvitedUser());
        invite.setStatus(1);
        inviteRepository.save(invite);
    }

    public void rejectInvite(Long inviteId) {
        Invite invite = inviteRepository.findById(inviteId).orElseThrow(
                () -> new RuntimeException("Invite not found.")
        );
        invite.setStatus(2);
        inviteRepository.save(invite);
    }

    public List<Group> getGroups() {
        return groupParticipantsRepository.findGroupByUserId(getCurrentUserId());
    }
}
