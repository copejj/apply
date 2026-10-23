package com.braindribbler.apply.repositories.logs;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.braindribbler.apply.models.logs.Week;

public interface WeekRepository extends JpaRepository<Week, Long> {
    @Query(value = """
                WITH week_logs AS (
                    SELECT week_id, user_id, COUNT(1) AS log_counts
                    FROM job_logs
                    WHERE user_id = :userId
                    GROUP BY week_id, user_id
                )
                SELECT
                    week_id AS weekId,
                    start_date AS startDate,
                    end_date AS endDate,
                    log_counts AS logCounts
                FROM weeks
                JOIN week_logs USING (week_id)
                ORDER BY start_date DESC
            """, nativeQuery = true)
    List<WeekProjection> findActiveWeeks(@Param("userId") Long userId);

    Optional<Week> findByWeekId(Long weekId);

    @Query("SELECT w FROM Week w WHERE w.startDate <= :date AND :date <= w.endDate")
    Optional<Week> findWeekByDate(@Param("date") LocalDate date);
}
