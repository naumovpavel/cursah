package com.wiftwift.model;

import java.math.BigDecimal;

import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.Relationship;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@RelationshipProperties
@Data
public class CreditRelationship {
    @Id @GeneratedValue
    private Long id;
    
    @Property
    private BigDecimal amount;
    
    @Relationship(type = "OWES", direction = Relationship.Direction.OUTGOING)
    private UserNode fromUser;
    
    @TargetNode
    private UserNode toUser;
}


