package com.wiftwift.model;

import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import lombok.Data;

import java.math.BigDecimal;

@RelationshipProperties
@Data
public class CreditRelationship {
    
    @RelationshipId
    private Long id;
    
    private BigDecimal creditAmount;
    private BigDecimal returnedAmount;
    private Boolean approved;
    
    @TargetNode
    private UserNode targetUser;
    
    public CreditRelationship() {
    }
    
    public CreditRelationship(BigDecimal creditAmount, BigDecimal returnedAmount, Boolean approved, UserNode targetUser) {
        this.creditAmount = creditAmount;
        this.returnedAmount = returnedAmount;
        this.approved = approved;
        this.targetUser = targetUser;
    }
}

