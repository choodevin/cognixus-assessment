CREATE TABLE todo (
    id UUID,
    dscp VARCHAR(500),
    isDone BOOLEAN,
    CONSTRAINT todo_id PRIMARY KEY(id)
);