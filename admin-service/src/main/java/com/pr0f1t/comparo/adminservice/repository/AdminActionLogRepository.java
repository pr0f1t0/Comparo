package com.pr0f1t.comparo.adminservice.repository;

import com.pr0f1t.comparo.adminservice.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {
    List<AdminActionLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);
}
