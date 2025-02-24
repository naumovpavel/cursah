package com.wiftwift.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "group_participants")
public class GroupParticipants {
    @ManyToOne
    @JoinColumn(name = "group_id")
    @MapsId("groupId")
    private Group group;

    @Column(name = "group_id")
    private Long groupId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    private User user;
    @Column(name = "user_id")
    private Long userId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
