CREATE TABLE todo_list (
    id UUID,
    user_id VARCHAR(100),
    todo_id UUID,
    CONSTRAINT todo_list_id PRIMARY KEY(id),
    CONSTRAINT fk_todo_id FOREIGN KEY(todo_id) REFERENCES todo(id)
);