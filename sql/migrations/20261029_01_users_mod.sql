BEGIN;
ALTER TABLE users 
    ADD COLUMN is_verified boolean NOT NULL DEFAULT false,
    ADD COLUMN verification_token uuid,
    ADD COLUMN reset_password_token uuid,
    ADD COLUMN reset_password_expires_at timestamp with time zone;
COMMIT;