BEGIN;
alter table job_logs
alter column description type text;

alter table job_logs
alter column notes type text;
COMMIT;