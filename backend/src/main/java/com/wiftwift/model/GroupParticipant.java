package com.wiftwift.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_participants")
@IdClass(GroupParticipantId.class)
public class GroupParticipant {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "group_id")
    private Long groupId;
}
