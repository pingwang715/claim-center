CREATE TABLE IF NOT EXISTS users (
    user_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(500) NOT NULL,
    role VARCHAR(20) NOT NULL, -- CLAIMANT, ADJUSTER, MANAGER
    created_at  TIMESTAMP
    )

CREATE TABLE IF NOT EXISTS claims
(
    claim_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    amount      DECIMAL(15, 2) NOT NULL,
    status      VARCHAR(30) NOT NULL,

    created_by  BIGINT NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    closed_at   TIMESTAMP,

    CONSTRAINT fk_claim_created_by
        FOREIGN KEY (created_by) REFERENCES users(user_id)
    )

CREATE TABLE IF NOT EXISTS claim_assignments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,

    claim_id    BIGINT NOT NULL,
    adjuster_id BIGINT NOT NULL,

    assigned_at TIMESTAMP,

    CONSTRAINT fk_assignment_claim
        FOREIGN KEY (claim_id) REFERENCES claims(claim_id),

    CONSTRAINT fk_assignment_adjuster
        FOREIGN KEY (adjuster_id) REFERENCES users(user_id)
    )

CREATE TABLE IF NOT EXISTS claim_histories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,

    claim_id    BIGINT NOT NULL,

    action_type VARCHAR(30) NOT NULL,
    old_status  VARCHAR(30),
    new_status  VARCHAR(30),

    performed_by BIGINT NOT NULL,

    notes       VARCHAR(2000),

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_history_claim
        FOREIGN KEY (claim_id) REFERENCES claims(claim_id),

    CONSTRAINT fk_history_user
        FOREIGN KEY (performed_by) REFERENCES users(user_id)
    )

CREATE INDEX idx_claim_status ON claims(status);
CREATE INDEX idx_claim_created_by ON claims(created_by);
CREATE INDEX idx_history_claim ON claim_histories(claim_id);

ALTER TABLE users
ADD COLUMN first_name VARCHAR(50),
ADD COLUMN last_name VARCHAR(50);

ALTER TABLE claim_assignments
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;