CREATE TABLE todo (
    id UUID,
    dscp VARCHAR(500),
    isDone BOOLEAN,
    user_id UUID,
    CONSTRAINT todo_id PRIMARY KEY(id),
    CONSTRAINT fk_user_id FOREIGN KEY(user_id) REFERENCES users(id)
);