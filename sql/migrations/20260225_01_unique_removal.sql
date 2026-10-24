BEGIN;
ALTER TABLE job_log_statuses
DROP CONSTRAINT unique_job_log_id_status_id;
COMMIT;
