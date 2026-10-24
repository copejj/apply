BEGIN;
alter table job_logs
add column description text;

alter table job_logs
add column job_link text;
COMMIT;