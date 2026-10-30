BEGIN;
DO $$
DECLARE
    r RECORD;
    max_id BIGINT;
    seq_name TEXT;
BEGIN
    -- Loop through all tables and columns that are backed by a sequence in the public schema
    FOR r IN 
        SELECT table_schema, table_name, column_name
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
          AND (column_default LIKE 'nextval%' OR identity_generation IS NOT NULL)
    LOOP
        -- 1. Safely resolve the exact sequence object name for the column
        seq_name := pg_get_serial_sequence(quote_ident(r.table_schema) || '.' || quote_ident(r.table_name), r.column_name);
        
        IF seq_name IS NOT NULL THEN
            -- 2. Dynamically fetch the current MAX(id) from that specific table
            EXECUTE format('SELECT COALESCE(MAX(%I), 0) FROM %I.%I', r.column_name, r.table_schema, r.table_name) INTO max_id;
            
            -- 3. If the table has rows, fast-forward the sequence to match the max ID
            IF max_id > 0 THEN
                PERFORM setval(seq_name, max_id);
            END IF;
        END IF;
    END LOOP;
END $$;
COMMIT;
