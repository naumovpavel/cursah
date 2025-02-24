package com.wiftwift.dto;

import com.wiftwift.model.Group;
import com.wiftwift.model.User;
import lombok.Data;

import java.util.List;
@Data
public class GroupResponse {
    private Group group;
    private List<User> users;
}
