package com.wiftwift.controller;

import com.wiftwift.model.Group;
import com.wiftwift.model.Invite;
import com.wiftwift.model.User;
import com.wiftwift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/myInvites")
    public List<Invite> getMyInvites() {
        return userService.getMyInvites();
    }

    @PostMapping("/myInvites/{inviteId}/approve")
    public ResponseEntity<String> approveInvite(@PathVariable Long inviteId) {
       userService.approveInvite(inviteId);
       return ResponseEntity.ok("Approved");
    }

    @PostMapping("/myInvites/{inviteId}/reject")
    public ResponseEntity<String> rejectInvites(@PathVariable Long inviteId) {
        userService.rejectInvite(inviteId);
        return ResponseEntity.ok("Rejected");
    }

    @GetMapping("/groups")
    public List<Group> getGroups() {
        return userService.getGroups();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAll();
    }
}
