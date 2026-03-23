package com.pr0f1t.comparo.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "admin_action_logs")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @Column(nullable = false)
    private String adminId;

    @Column(nullable = false)
    private String actionType;

    @Column(nullable = false)
    private String targetId;

    private String details;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

}
