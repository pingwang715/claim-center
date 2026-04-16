INSERT INTO users(email, password_hash, role, created_at)
VALUES
    ('test@gmail.com', '...', 'CLAIMANT', CURRENT_TIMESTAMP),
    ('adjuster@gmail.com', '...', 'ADJUSTER', CURRENT_TIMESTAMP),
    ('manager@gmail.com', '...', 'MANAGER', CURRENT_TIMESTAMP);

INSERT INTO claims(title, description, amount, status, created_by, created_at, updated_at, closed_at)
values ('Iphone stolen', 'My iphone was stolen in the metro during my visit in Paris.', 200, 'SUBMITTED', 1, CURRENT_TIMESTAMP, NULL, NULL);

INSERT INTO claim_histories(claim_id, action_type, old_status, new_status, performed_by, notes, created_at)
VALUES (1, 'SUBMITTED', NULL, 'SUBMITTED', 1, 'Created claim, will be examined', CURRENT_TIMESTAMP);

INSERT INTO claim_assignments(claim_id, adjuster_id, assigned_at)
VALUES (1, 2, CURRENT_TIMESTAMP);

UPDATE users
SET first_name = 'John', last_name = 'Doe'
WHERE user_id = 1;

UPDATE users
SET first_name = 'Jane', last_name = 'Doe'
WHERE user_id = 2;

UPDATE users
SET first_name = 'Paul', last_name = 'Doe'
WHERE user_id = 3;