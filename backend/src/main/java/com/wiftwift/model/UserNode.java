package com.wiftwift.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import lombok.Data;

@Node
@Data
public class UserNode {
    @Id
    private Long id;
    private String name;
}

