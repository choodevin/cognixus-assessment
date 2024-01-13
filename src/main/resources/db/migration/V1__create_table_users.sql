CREATE TABLE users (
    id UUID,
    token_expire TIMESTAMP,
    email VARCHAR(100),
    CONSTRAINT user_id PRIMARY KEY(id)
);