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
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.wiftwift.dto.CreditDTO;
import com.wiftwift.exception.NotFoundException;
import com.wiftwift.model.Credit;
import com.wiftwift.model.Expense;
import com.wiftwift.model.ExpenseParticipant;
import com.wiftwift.model.Group;
import com.wiftwift.model.UserNode;
import com.wiftwift.repository.CreditRepository;
import com.wiftwift.repository.ExpenseParticipantRepository;
import com.wiftwift.repository.ExpenseRepository;
import com.wiftwift.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

@Service
@Transactional
public class CreditService {
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseParticipantRepository expenseParticipantRepository;
    @Autowired
    private UserRepository userRepository;

    private CreditDTO creditToCreditDTO(Credit credit) {
        return new CreditDTO(credit.getRelationshipId(), credit.getFromUser(), credit.getToUser(), credit.getCreditAmount(), credit.getReturnedAmount(), credit.getApproved());
    }

    public List<CreditDTO> getCreditsTo(Long userId) {
        return creditRepository.findIncomingCreditDTOsByUserId(userId).stream()
        .map(this::creditToCreditDTO)
        .collect(Collectors.toList());
    }
    
    public List<CreditDTO> getCreditsFrom(Long userId) {
        return creditRepository.findOutgoingCreditDTOsByUserId(userId).stream()
        .map(this::creditToCreditDTO)
        .collect(Collectors.toList());
    }

    

    public void returnCredit(String creditId, BigDecimal amount) {
        Credit credit = creditRepository.findCreditRelationshipById(creditId)
                .orElseThrow(() -> new NotFoundException("долг не найден"));

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

        creditRepository.updateCreditRelationship(credit.getRelationshipId(), credit.getCreditAmount(), credit.getReturnedAmount(),
                credit.getApproved());
    }

    public void approveCredit(String creditId) {
        Credit credit = creditRepository.findCreditRelationshipById(creditId)
                .orElseThrow(() -> new NotFoundException("долг не найден"));

        if (credit.getReturnedAmount() != null) {
            credit.setCreditAmount(credit.getCreditAmount().subtract(credit.getReturnedAmount()));
            if (credit.getCreditAmount().compareTo(new BigDecimal(0)) == -1
                    || credit.getCreditAmount().compareTo(new BigDecimal(0)) == 0) {
                creditRepository.deleteCreditRelationshipById(creditId);
                return;
            }
        }

        credit.setReturnedAmount(new BigDecimal(0));
        credit.setApproved(false);
        creditRepository.updateCreditRelationship(credit.getRelationshipId(), credit.getCreditAmount(), credit.getReturnedAmount(),
                credit.getApproved());
    }

    public void closeGroup(Group group) {
        List<Expense> expenses = expenseRepository.findByGroupId(group.getId());

        for (Expense expense : expenses) {
            List<ExpenseParticipant> participants = expenseParticipantRepository.findByExpenseId(expense.getId());

            int participantCount = 0;
            for (ExpenseParticipant participant : participants) {
                if (participant.getConfirmed()) {
                    participantCount++;
                }
            }

            BigDecimal participantShare = expense.getValue().divide(BigDecimal.valueOf(participantCount), 2, RoundingMode.HALF_UP);

            for (ExpenseParticipant participant : participants) {
                if (participant.getUserId().equals(group.getPaidBy())) {
                    continue;
                }

                if (!participant.getConfirmed()) {
                    continue;
                }

                if (!creditRepository.existsById(group.getPaidBy())) {
                    UserNode lender = new UserNode(group.getPaidBy());
                    creditRepository.save(lender);
                }

                if (!creditRepository.existsById(participant.getUserId())) {
                    UserNode borrower = new UserNode(participant.getUserId());
                    creditRepository.save(borrower);
                }

                creditRepository.createCreditRelationship(
                        participant.getUserId(),
                        group.getPaidBy(),
                        participantShare,
                        BigDecimal.ZERO,
                        false);
            }
        }

        optimizeCredits();
    }

    private void optimizeCredits() {
        List<Credit> allCredits = creditRepository.findAllCreditRelationships();

        Map<Long, Map<Long, Pair<BigDecimal, BigDecimal>>> graph = new HashMap<>();

        for (Credit credit : allCredits) {
            if (credit.getCreditAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }


            graph.putIfAbsent(credit.getFromUser(), new HashMap<>());
            if (graph.get(credit.getFromUser()).get(credit.getToUser()) != null) {
                var curCredit = graph.get(credit.getFromUser()).get(credit.getToUser());
                graph.get(credit.getFromUser()).put(credit.getToUser(), Pair.of(credit.getCreditAmount().add(curCredit.getFirst()), credit.getReturnedAmount().add(curCredit.getSecond())));
            } else {
                graph.get(credit.getFromUser()).put(credit.getToUser(), Pair.of(credit.getCreditAmount(), credit.getReturnedAmount()));
            }
        }

        optimizeCycles(graph);

        for (Credit credit : allCredits) {
            creditRepository.deleteCreditRelationshipById(credit.getRelationshipId());
        }

        for (Long from : graph.keySet()) {
            for (Map.Entry<Long, Pair<BigDecimal, BigDecimal>> entry : graph.get(from).entrySet()) {
                Long to = entry.getKey();
                BigDecimal amount = entry.getValue().getFirst();

                if (amount.compareTo(BigDecimal.ZERO) > 0) {
                    creditRepository.createCreditRelationship(
                            from,
                            to,
                            amount,
                            entry.getValue().getSecond(),
                            false
                    );
                }
            }
        }
    }

    private void optimizeCycles(Map<Long, Map<Long, Pair<BigDecimal, BigDecimal>>> graph) {
        Set<Long> allNodes = new HashSet<>(graph.keySet());
        for (Map<Long, Pair<BigDecimal, BigDecimal>> edges : graph.values()) {
            allNodes.addAll(edges.keySet());
        }

        boolean cycleFound;
        do {
            cycleFound = false;

            for (Long startNode : allNodes) {
                if (!graph.containsKey(startNode))
                    continue;

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

    private List<Long> findCycle(Map<Long, Map<Long, Pair<BigDecimal, BigDecimal>>> graph, Long startNode) {
        Map<Long, Long> parent = new HashMap<>();
        Set<Long> visited = new HashSet<>();
        Set<Long> inStack = new HashSet<>();

        return dfs(graph, startNode, parent, visited, inStack);
    }

    private List<Long> dfs(Map<Long, Map<Long, Pair<BigDecimal, BigDecimal>>> graph, Long node,
            Map<Long, Long> parent, Set<Long> visited, Set<Long> inStack) {
        visited.add(node);
        inStack.add(node);

        if (graph.containsKey(node)) {
            for (Long neighbor : new ArrayList<>(graph.get(node).keySet())) {
                var amount = graph.get(node).get(neighbor).getFirst().subtract(graph.get(node).get(neighbor).getSecond());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
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

    private void reduceCycle(Map<Long, Map<Long, Pair<BigDecimal, BigDecimal>>> graph, List<Long> cycle) {
        BigDecimal minFlow = null;

        System.out.println(graph);
        System.out.println(cycle);

        for (int i = 0; i < cycle.size() - 1; i++) {
            Long from = cycle.get(i);
            Long to = cycle.get(i + 1);
            if (!graph.containsKey(from) || !graph.get(from).containsKey(to)) {
                return;
            }

            var flow = graph.get(from).get(to).getFirst().subtract(graph.get(from).get(to).getSecond());
            if (minFlow == null || flow.compareTo(minFlow) < 0) {
                minFlow = flow;
            }
        }

        System.out.println(minFlow);

        for (int i = 0; i < cycle.size() - 1; i++) {
            Long from = cycle.get(i);
            Long to = cycle.get(i + 1);

            BigDecimal newFlow = graph.get(from).get(to).getFirst().subtract(minFlow);
            if (newFlow.compareTo(BigDecimal.ZERO) <= 0) {
                graph.get(from).remove(to);
                if (graph.get(from).isEmpty()) {
                    graph.remove(from);
                }
            } else {
                graph.get(from).put(to, Pair.of(newFlow, graph.get(from).get(to).getSecond()));
            }
        }
    }
}
