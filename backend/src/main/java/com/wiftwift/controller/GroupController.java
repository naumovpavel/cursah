package com.wiftwift.controller;

import com.wiftwift.dto.GroupRequest;
import com.wiftwift.dto.GroupResponse;
import com.wiftwift.dto.InviteRequest;
import com.wiftwift.model.Expense;
import com.wiftwift.model.Group;
import com.wiftwift.service.ExpenseService;
import com.wiftwift.service.GroupService;
import com.wiftwift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private UserService userService;


    @PostMapping("/new")
    public Group createGroup(@RequestBody GroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setPaidBy(request.getPaidBy());
        return groupService.createGroup(group, userService.getCurrentUser());
    }

    @PostMapping("/invite")
    public ResponseEntity<String> inviteUser(@RequestBody InviteRequest request) {
        groupService.invitePerson(request);
        return ResponseEntity.ok("Successfully invited");
    }

    @GetMapping("/{groupId}/expense")
    public List<Expense> getExpenses(@PathVariable Long groupId) {
        return expenseService.getExpensesBiGroupId(groupId);
    }

    @GetMapping("/{groupId}")
    public GroupResponse getGroups(@PathVariable Long groupId) {
        return groupService.findById(groupId);
    }

    @PostMapping("/{groupId}/setPaidBy/{userId}")
    public ResponseEntity<String> setPaidBy(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.setPaidBy(groupId, userId);
        return ResponseEntity.ok("Successfully set paid by");
    }
}

