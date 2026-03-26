-- ============================================================
--  DocVerify – Database Schema
--  Run this once before launching the application.
--  Database: MySQL 8+
-- ============================================================

CREATE DATABASE IF NOT EXISTS docverify_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE docverify_db;

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,          -- SHA-256 hex
    email         VARCHAR(100) NOT NULL UNIQUE,
    role          ENUM('ADMIN','VERIFIER','VIEWER') NOT NULL DEFAULT 'VIEWER',
    status        ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login    DATETIME
);

-- Default admin  (password = Admin@123)
INSERT IGNORE INTO users (username, password_hash, email, role, status)
VALUES (
    'admin',
    SHA2('Admin@123', 256),
    'admin@docverify.local',
    'ADMIN',
    'ACTIVE'
);

-- ── Documents ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS documents (
    doc_id          INT AUTO_INCREMENT PRIMARY KEY,
    submitted_by    INT          NOT NULL,
    doc_type        ENUM('AADHAAR','PAN','DRIVING_LICENSE','BIRTH_CERTIFICATE') NOT NULL,
    doc_number      VARCHAR(30)  NOT NULL,
    holder_name     VARCHAR(100) NOT NULL,
    dob             DATE,
    is_locked       BOOLEAN NOT NULL DEFAULT FALSE,
    submitted_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submitted_by) REFERENCES users(user_id)
);

-- ── Verification Attempts ────────────────────────────────────
CREATE TABLE IF NOT EXISTS verification_attempts (
    attempt_id     INT AUTO_INCREMENT PRIMARY KEY,
    doc_id         INT  NOT NULL,
    verified_by    INT  NOT NULL,
    attempt_number INT  NOT NULL DEFAULT 1,
    attempted_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doc_id)      REFERENCES documents(doc_id),
    FOREIGN KEY (verified_by) REFERENCES users(user_id)
);

-- ── Verification Results ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS verification_results (
    result_id        INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id       INT NOT NULL,
    doc_id           INT NOT NULL,
    status           ENUM('PASSED','FAILED') NOT NULL,
    rejection_reason VARCHAR(500),
    failed_rule      VARCHAR(100),
    verified_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES verification_attempts(attempt_id),
    FOREIGN KEY (doc_id)     REFERENCES documents(doc_id)
);

-- ── Audit Log ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS audit_log (
    log_id     INT AUTO_INCREMENT PRIMARY KEY,
    action     VARCHAR(50)  NOT NULL,
    target     VARCHAR(100) NOT NULL,
    details    VARCHAR(500),
    logged_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
