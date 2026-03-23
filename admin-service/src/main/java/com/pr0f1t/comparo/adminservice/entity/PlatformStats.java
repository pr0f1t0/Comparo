package com.pr0f1t.comparo.adminservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "platform_stats")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlatformStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate statDate;

    private Long totalUsers;
    private Long totalProducts;
    private Long pendingReviews;

}
