package com.wiftwift.repository;

import com.wiftwift.model.Credit;
import com.wiftwift.model.CreditRelationship;
import com.wiftwift.model.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditRepository extends Neo4jRepository<UserNode, Long> {

       @Query("MATCH (u:User)-[r:CREDIT]->(target:User) " +
                     "RETURN u.id as fromUser, target.id as toUser, " +
                     "r.creditAmount as creditAmount, r.returnedAmount as returnedAmount, " +
                     "r.approved as approved,  toString(elementId(r)) as relationshipId")
       List<Credit> findAllCreditRelationships();

       @Query("MATCH (u:User)-[r:CREDIT]->(target:User) " +
                     "WHERE u.id = $userId " +
                     "RETURN u.id as fromUser, target.id as toUser, " +
                     "r.creditAmount as creditAmount, r.returnedAmount as returnedAmount, " +
                     "r.approved as approved,  toString(elementId(r)) as relationshipId")
       List<Credit> findOutgoingCreditDTOsByUserId(@Param("userId") Long userId);

       @Query("MATCH (source:User)-[r:CREDIT]->(u:User) " +
                     "WHERE u.id = $userId " +
                     "RETURN source.id as fromUser, u.id as toUser, " +
                     "r.creditAmount as creditAmount, r.returnedAmount as returnedAmount, " +
                     "r.approved as approved, toString(elementId(r)) as relationshipId")
       List<Credit> findIncomingCreditDTOsByUserId(@Param("userId") Long userId);

       @Query("MATCH ()-[r:CREDIT]->() " +
                     "WHERE elementId(r) = $relationshipId " +
                     "RETURN r.creditAmount as creditAmount, r.returnedAmount as returnedAmount, " +
                     "r.approved as approved, toString(elementId(r)) as relationshipId")
       Optional<Credit> findCreditRelationshipById(@Param("relationshipId") String relationshipId);

       @Query("MATCH (from:User), (to:User) " +
                     "WHERE from.id = $fromUserId AND to.id = $toUserId " +
                     "CREATE (from)-[r:CREDIT {creditAmount: $creditAmount, returnedAmount: $returnedAmount, approved: $approved}]->(to) "
                     +
                     "RETURN r")
       CreditRelationship createCreditRelationship(
                     @Param("fromUserId") Long fromUserId,
                     @Param("toUserId") Long toUserId,
                     @Param("creditAmount") BigDecimal creditAmount,
                     @Param("returnedAmount") BigDecimal returnedAmount,
                     @Param("approved") Boolean approved);

       @Query("MATCH (from:User)-[r:CREDIT]->(to:User) " +
                     "WHERE  elementId(r) = $relationshipId " +
                     "SET r.creditAmount = $creditAmount, r.returnedAmount = $returnedAmount, r.approved = $approved " +
                     "RETURN r")
       CreditRelationship updateCreditRelationship(
                     @Param("relationshipId") String relationshipId,
                     @Param("creditAmount") BigDecimal creditAmount,
                     @Param("returnedAmount") BigDecimal returnedAmount,
                     @Param("approved") Boolean approved);

       @Query("MATCH ()-[r:CREDIT]->() " +
                     "WHERE  elementId(r) = $relationshipId " +
                     "DELETE r")
       void deleteCreditRelationshipById(@Param("relationshipId") String relationshipId);

       @Query("MATCH (source:User)-[r:CREDIT]->() " +
                     "WHERE  elementId(r) = $relationshipId " +
                     "RETURN source")
       Optional<UserNode> findSourceUserByRelationshipId(@Param("relationshipId") String relationshipId);
}
