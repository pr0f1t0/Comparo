package com.pr0f1t.comparo.adminservice.repository;

import com.pr0f1t.comparo.adminservice.entity.PlatformStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PlatformStatsRepository extends JpaRepository<PlatformStats, Long> {
    Optional<PlatformStats> findByStatDate(LocalDate statDate);
}
