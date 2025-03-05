package com.wiftwift.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("User")
public class UserNode {
    
    @Id
    private Long id;
    
    @Relationship(type = "CREDIT", direction = Relationship.Direction.OUTGOING)
    private Set<CreditRelationship> outgoingCredits = new HashSet<>();
    
    @Relationship(type = "CREDIT", direction = Relationship.Direction.INCOMING)
    private Set<CreditRelationship> incomingCredits = new HashSet<>();
    
    public UserNode() {
    }
    
    public UserNode(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Set<CreditRelationship> getOutgoingCredits() {
        return outgoingCredits;
    }
    
    public void setOutgoingCredits(Set<CreditRelationship> outgoingCredits) {
        this.outgoingCredits = outgoingCredits;
    }
    
    public Set<CreditRelationship> getIncomingCredits() {
        return incomingCredits;
    }
    
    public void setIncomingCredits(Set<CreditRelationship> incomingCredits) {
        this.incomingCredits = incomingCredits;
    }
}