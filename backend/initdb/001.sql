CREATE TABLE users (
   id SERIAL PRIMARY KEY,
   username VARCHAR(255) NOT NULL,
   password TEXT NOT NULL,
   role TEXT,
   credit_total money DEFAULT 0,
   debt_total money DEFAULT 0
);

CREATE TABLE groups (
   id SERIAL PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   closed_at TIMESTAMP,
   should_be_paid_by INT REFERENCES users(id),
   paid_by INT REFERENCES users(id),
   description TEXT
);

CREATE TABLE group_participants (
   user_id INT REFERENCES users(id) ON DELETE CASCADE,
   group_id INT REFERENCES groups(id) ON DELETE CASCADE,
   PRIMARY KEY (user_id, group_id)
);

CREATE OR REPLACE FUNCTION find_user_for_payment(gpid BIGINT)
RETURNS TABLE (
   selected_user_id BIGINT,
   total_payments BIGINT,
   score FLOAT8
) AS $$
BEGIN
   RETURN QUERY
   WITH user_payments AS (
       SELECT
           g.paid_by AS user_id,
           COUNT(*) AS total_payments
       FROM groups g
       GROUP BY g.paid_by
   )
   SELECT
       u.id AS selected_user_id,
       COALESCE(up.total_payments, 0) AS total_payments,
       (COALESCE(up.total_payments, 0) + ABS(u.debt_total::numeric::FLOAT8 - u.credit_total::numeric::FLOAT8)) AS score
   FROM group_participants gp
   JOIN users u ON gp.user_id = u.id
   LEFT JOIN user_payments up ON u.id = up.user_id
   WHERE gp.group_id = gpid
   ORDER BY score ASC, total_payments DESC
   LIMIT 1;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION set_should_be_paid_by()
RETURNS TRIGGER AS $$
DECLARE
   should_be INT;
BEGIN
   SELECT selected_user_id
   INTO should_be
   FROM find_user_for_payment(NEW.group_id);


   UPDATE groups
   SET should_be_paid_by = should_be
   WHERE id = NEW.group_id;


   RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER user_insterted_to_group
AFTER INSERT ON group_participants
FOR EACH ROW
EXECUTE FUNCTION set_should_be_paid_by();