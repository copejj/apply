# Deployment Guide: Shadow Schema Migrations and Hosting

This document details the automated deployment workflow, database migration framework, and environment configurations utilized to deploy the Applied Jobs Log Portal to the production Linux environment.

---

## Deployment Architecture

The application implements a secure, off-server build architecture combined with a zero-downtime, safe schema migration script:

1. **Compilation Phase:** The production code is securely checked out and compiled inside an isolated GitHub Actions cloud runner container (`ubuntu-24.04`), packaging the application into a `.war` file without consuming production server CPU resources.
2. **Transfer Phase:** The pre-compiled `.war` bundle is securely copied to the production target hosting server over SSH.
3. **Database Phase (Shadow Isolation):** If a new database delta is discovered, a complete replica of the active schema is extracted into an isolated tracking layer named `public_shadow`. Migrations run inside this boundary before an atomic table-swap occurs.
4. **App Container Phase:** The versioned, timestamped runtime file is moved directly into the Tomcat parallel deployment directory to complete a zero-downtime hot swap.

---

## Database Migration Subsystem

To manage structural state changes reliably, the automation engine checks a central ledger table named `public.schema_migrations`.

### Migration Naming Convention

To ensure scripts are executed in the correct chronological order and are picked up by the automation engine, all files must follow this format:
```text
YYYYMMDD_[Number]_(ShortDescription).sql
```

#### Format Breakdown
* **YYYYMMDD:** The 8-digit date the migration was created (e.g., `20261029`).
* **_:** A literal underscore separator.
* **[Number]:** A sequential tracking integer (1, 2, 3...) to handle multiple migrations created on the same day.
* **(ShortDescription):** A brief, slug-style description of the structural changes (e.g., `add_user_index`).
* **.sql:** The required file extension.

#### Valid Examples
* `20261029_1_init_auth_tables.sql`
* `20261029_2_add_email_column.sql`
* `20261105_1_update_constraints.sql`

#### Invalid Examples
* `update_v1.sql` (Missing date-based sequential prefix)
* `2026-10-29-migration.sql` (Uses dashes instead of underscores)
* `10292026_1_fix.sql` (Violates the chronological YYYYMMDD ordering schema)

---

## Production Deployment Flow

The remote update lifecycle executes across 6 distinct phases managed automatically by the deployment script.

### Step 1: Initialization
Establishes environment baselines and configures core PostgreSQL argument patterns (`ON_ERROR_STOP=1`) to immediately halt execution if any statement experiences syntax or dependency constraints.

### Step 2: Source Checkout
Fetches upstream repository metadata and explicitly forces a clean workspace branch synchronized with the targeting deployment tag release.

### Step 3: Database Evaluation & Shadow Setup
Compares local disk SQL files against the `schema_migrations` table log. If anomalies or new files are found:
* Clears out any stale `public_shadow` and `public_old` definitions.
* Dumps a snapshot of the live table structure.
* Alters the dump's text namespaces to point entirely to `public_shadow`.
* Re-injects the isolated structural copy into the database, keeping production live.

### Step 4: Schema Execution & Atomic Swapping
Executes the base structural schema initialization (`sql/init_schema.sql`) inside the isolated shadow schema context. Next, it iterates through all uncompleted delta scripts.

If all operations pass cleanly:
* Grants schema runtime usage privileges back to the core tracking database user.
* Executes an atomic swap inside a single transaction block:
  ```sql
  BEGIN; 
  ALTER SCHEMA public RENAME TO public_old; 
  ALTER SCHEMA public_shadow RENAME TO public; 
  COMMIT;
  ```

### Step 5: Tomcat Versioned Hot-Deployment
Saves the pre-compiled application bundle using Apache Tomcat's parallel deployment naming format:
```text
[ContextPath]##[TagName].war
```
Tomcat automatically detects this structure, boots the new code container in parallel, routes all incoming new sessions to the updated package, and gracefully turns off the older version once its active user counts hit zero.

### Step 6: Workspace Cleanup
Clears raw build file traces from the target system environment and resets git workspace flags to restore baseline stability.

---

## Failure Recovery Lifecycle

If a database script failure occurs during **Step 4**, an automated recovery sequence executes instantly:
1. The execution error trigger catches the failure state before an atomic structural swap is attempted.
2. The partial tracking sandbox schema (`public_shadow`) is immediately destroyed via a cascading `DROP SCHEMA` command to prevent database pollution.
3. The server workspace performs a local git reset to restore code configurations to their previous stable operating states.
