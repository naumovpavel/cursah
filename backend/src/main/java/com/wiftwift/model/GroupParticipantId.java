package com.wiftwift.model;

import java.io.Serializable;
import java.util.Objects;

public class GroupParticipantId implements Serializable {
    private Long userId;
    private Long groupId;

    public GroupParticipantId() {
    }

    public GroupParticipantId(Long userId, Long groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupParticipantId)) return false;
        GroupParticipantId that = (GroupParticipantId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, groupId);
    }
}
