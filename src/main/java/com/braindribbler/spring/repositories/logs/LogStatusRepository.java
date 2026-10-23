package com.braindribbler.spring.repositories.logs;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.braindribbler.spring.models.logs.LogStatus;

public interface LogStatusRepository extends JpaRepository<LogStatus, Long> {
    Optional<LogStatus> findByLogLogId(Long logId);

    void deleteByLogLogId(Long logId);
}
