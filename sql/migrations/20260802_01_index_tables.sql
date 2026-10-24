BEGIN;
-- Speed up the main query lookups, filters, and logs sorting
CREATE INDEX idx_job_logs_lookup 
ON job_logs (user_id, week_id, company_id, action_date ASC, title ASC);

-- Speed up the company name sorting lookups
CREATE INDEX idx_companies_name 
ON companies (company_id, name ASC);

-- Speed up the nested "latest status" subquery tracking
CREATE INDEX idx_log_statuses_latest 
ON job_log_statuses (job_log_id, status_date DESC);
COMMIT;