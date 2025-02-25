package com.wiftwift.model;

import java.math.BigDecimal;

import java.math.BigDecimal;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import lombok.Data;
import lombok.NoArgsConstructor;


@Node
@Data
@NoArgsConstructor
public class CreditNode {
    @Id @GeneratedValue
    private Long id;
    
    private Long fromUser;
    private Long toUser;
    private BigDecimal creditAmount;
    private BigDecimal returnedAmount;
    private Boolean approved;
}

