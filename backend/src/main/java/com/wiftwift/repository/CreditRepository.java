package com.wiftwift.repository;

import org.springframework.stereotype.Repository;
import com.wiftwift.model.CreditNode;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;

@Repository
public interface CreditRepository extends Neo4jRepository<CreditNode, Long> {
    List<CreditNode> findByToUser(Long userId);
    List<CreditNode> findByFromUser(Long userId);
    List<CreditNode> findByApproved(Boolean approved);

}
