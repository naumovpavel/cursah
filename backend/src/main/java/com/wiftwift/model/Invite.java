package com.wiftwift.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invites")
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "invited_user", nullable = false)
    private String invitedUser;

    @Column(name = "from_user", nullable = false)
    private String fromUser;

    @Column(name = "status", nullable = false)
    private Integer status = 0;
}
