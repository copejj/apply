BEGIN;
CREATE INDEX idx_job_logs_user_week ON job_logs (user_id, week_id);
COMMIT;