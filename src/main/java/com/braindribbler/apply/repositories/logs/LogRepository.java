package com.braindribbler.apply.repositories.logs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.braindribbler.apply.models.logs.Log;

public interface LogRepository extends JpaRepository<Log, Long> {

    Optional<Log> findByLogId(Long logId);

    Optional<Log> findByLogIdAndUserId(Long logId, Long userId);

    List<Log> findByUserId(Long userId);

    @Query("SELECT DISTINCT l FROM Log l " +
            "LEFT JOIN FETCH l.company c " +
            "WHERE l.userId = :userId " +
            "AND (:weekId IS NULL OR l.weekId = :weekId) " +
            "AND (:companyId IS NULL OR c.companyId = :companyId)" +
            "ORDER BY l.actionDate ASC, c.companyName ASC, l.title ASC")
    List<Log> findFilteredLogs(
            @Param("userId") Long userId,
            @Param("weekId") Long weekId,
            @Param("companyId") Long companyId);
}
