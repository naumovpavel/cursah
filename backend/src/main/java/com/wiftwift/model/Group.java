package com.wiftwift.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "should_be_paid_by")
    private Long shouldBePaidBy;

    @Column(name = "paid_by")
    private Long paidBy;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "group")
    private List<Expense> expenses;
}
