package com.wiftwift.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import com.wiftwift.model.CreditNode;
import com.wiftwift.model.Expense;
import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.Group;
import com.wiftwift.repository.CreditRepository;
import com.wiftwift.repository.ExpenseParticipantRepository;
import com.wiftwift.repository.ExpenseRepository;
import com.wiftwift.repository.GroupRepository;
import com.wiftwift.repository.UserRepository;
import org.springframework.data.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CreditService {
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;
    @Autowired
    private UserRepository userRepository;

    public List<CreditNode> getCreditsTo(Long userId) {
        return creditRepository.findByToUser(userId);
    }

    public List<CreditNode> getCreditsFrom(Long userId) {
        return creditRepository.findByFromUser(userId);
    }

    public CreditNode returnCredit(Long creditId, BigDecimal amount) {
        CreditNode credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found"));

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (amount.compareTo(new BigDecimal(0)) != 1) {
            throw new IllegalArgumentException("Amount cannot be less than 0");
        }

        if (credit.getReturnedAmount() == null) {
            credit.setReturnedAmount(amount);
        } else {
            credit.setReturnedAmount(credit.getReturnedAmount().add(amount));
        }

        credit.setApproved(false);

        return creditRepository.save(credit);
    }

    public void approveCredit(Long creditId) {
        CreditNode credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found"));

        if (credit.getReturnedAmount() != null) {
            credit.setCreditAmount(credit.getCreditAmount().subtract(credit.getReturnedAmount()));
            if (credit.getCreditAmount().compareTo(new BigDecimal(0)) == -1 || credit.getCreditAmount().compareTo(new BigDecimal(0)) == 0) {
                creditRepository.delete(credit);
                return;
            }
        }

        credit.setReturnedAmount(new BigDecimal(0));
        credit.setApproved(true);
        creditRepository.save(credit);
    }

    public void closeGroup(Long groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        Group group = groupRepository.getById(groupId);
        if (group.getClosedAt() != null) {
            throw new RuntimeException("group already closed");
        }
        if (group.getPaidBy() < 1) {
            throw new RuntimeException("Невыбран пользователь оплативший траты");
        }
        group.setClosedAt(java.time.LocalDateTime.now());
        groupRepository.save(group);

        for (Expense expense : expenses) {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());

            BigDecimal participantShare = expense.getValue()
                    .divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);

            for (ExpenseParticipant participant : participants) {
                if (participant.getUserId().equals(group.getPaidBy())) {
                    continue;
                }

                CreditNode credit = new CreditNode();
                credit.setFromUser(participant.getUserId());
                credit.setToUser(group.getPaidBy());
                credit.setCreditAmount(participantShare);
                credit.setReturnedAmount(BigDecimal.ZERO);
                credit.setApproved(false);

                creditRepository.save(credit);

                var user = userRepository.getById(participant.getUserId());
                if (user.getCreditTotal() == null) {
                    user.setCreditTotal(participantShare);
                } else {
                    user.setCreditTotal(user.getCreditTotal().add(participantShare));
                }
                userRepository.save(user);

                user = userRepository.getById(group.getPaidBy());
                if (user.getDebtTotal() == null) {
                    user.setDebtTotal(participantShare);
                } else {
                    user.setDebtTotal(user.getDebtTotal().add(participantShare));
                }
                userRepository.save(user);
            }
        }

        optimizeCredits();
    }

    private void optimizeCredits() {
        List<CreditNode> allCredits = creditRepository.findByApproved(false);
        
        Map<Long, Map<Long, BigDecimal>> graph = new HashMap<>();
        
        for (CreditNode credit : allCredits) {
            if (credit.getCreditAmount().compareTo(BigDecimal.ZERO) <= 0)
                continue;
            
            Long from = credit.getFromUser();
            Long to = credit.getToUser();
            BigDecimal amount = credit.getCreditAmount();
            
            graph.putIfAbsent(from, new HashMap<>());
            graph.get(from).put(to, amount);
        }
        
        optimizeCycles(graph);
        
        for (CreditNode credit : allCredits) {
            creditRepository.delete(credit);
        }
        
        for (Long from : graph.keySet()) {
            for (Map.Entry<Long, BigDecimal> entry : graph.get(from).entrySet()) {
                Long to = entry.getKey();
                BigDecimal amount = entry.getValue();
                
                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    CreditNode newCredit = new CreditNode();
                    newCredit.setFromUser(from);
                    newCredit.setToUser(to);
                    newCredit.setCreditAmount(amount);
                    newCredit.setReturnedAmount(BigDecimal.ZERO);
                    newCredit.setApproved(false);
                    
                    creditRepository.save(newCredit);
                }
            }
        }
    }
    
    private void optimizeCycles(Map<Long, Map<Long, BigDecimal>> graph) {
        Set<Long> allNodes = new HashSet<>(graph.keySet());
        for (Map<Long, BigDecimal> edges : graph.values()) {
            allNodes.addAll(edges.keySet());
        }
        
        boolean cycleFound;
        do {
            cycleFound = false;
            
            for (Long startNode : allNodes) {
                if (!graph.containsKey(startNode)) continue;
                
                List<Long> cycle = findCycle(graph, startNode);
                
                if (cycle != null && cycle.size() > 1) {
                    Collections.reverse(cycle);
                    reduceCycle(graph, cycle);
                    cycleFound = true;
                    break;
                }
            }
        } while (cycleFound);
    }
    
    private List<Long> findCycle(Map<Long, Map<Long, BigDecimal>> graph, Long startNode) {
        Map<Long, Long> parent = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        Set<Long> inStack = new HashSet<>();
        
        return dfs(graph, startNode, parent, visited, inStack);
    }
    
    private List<Long> dfs(Map<Long, Map<Long, BigDecimal>> graph, Long node, 
                          Map<Long, Long> parent, Set<Long> visited, Set<Long> inStack) {
        visited.add(node);
        inStack.add(node);
        
        if (graph.containsKey(node)) {
            for (Long neighbor : new ArrayList<>(graph.get(node).keySet())) {
                if (graph.get(node).get(neighbor).compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                
                if (!visited.contains(neighbor)) {
                    parent.put(neighbor, node);
                    List<Long> cycle = dfs(graph, neighbor, parent, visited, inStack);
                    if (cycle != null) {
                        return cycle;
                    }
                } 

                else if (inStack.contains(neighbor)) {
                    List<Long> cycle = new ArrayList<>();
                    cycle.add(neighbor);
                    
                    Long current = node;
                    while (current != null && !current.equals(neighbor)) {
                        cycle.add(current);
                        current = parent.get(current);
                    }
                    
                    if (current != null) {
                        cycle.add(neighbor);
                        return cycle;
                    }
                }
            }
        }
        
        inStack.remove(node);
        return null;
    }
    
    private void reduceCycle(Map<Long, Map<Long, BigDecimal>> graph, List<Long> cycle) {
        BigDecimal minFlow = null;

        for (int i = 0; i < cycle.size() - 1; i++) {
            Long from = cycle.get(i);
            Long to = cycle.get(i + 1);
            if (!graph.containsKey(from) || !graph.get(from).containsKey(to)) {
                return;
            }
    
            BigDecimal flow = graph.get(from).get(to);
            if (minFlow == null || flow.compareTo(minFlow) < 0) {
                minFlow = flow;
            }
        }

        for (int i = 0; i < cycle.size() - 1; i++) {
            Long from = cycle.get(i);
            Long to = cycle.get(i + 1);

            BigDecimal newFlow = graph.get(from).get(to).subtract(minFlow);
            if (newFlow.compareTo(BigDecimal.ZERO) <= 0) {
                graph.get(from).remove(to);
                if (graph.get(from).isEmpty()) {
                    graph.remove(from);
                }
            } else {
                graph.get(from).put(to, newFlow);
            }
        }
    }
}
