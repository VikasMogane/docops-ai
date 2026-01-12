CREATE TABLE workflow_comment (
    id BIGSERIAL PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    comment TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_comment_workflow
        FOREIGN KEY (workflow_instance_id)
        REFERENCES workflow_instance(id)
);
