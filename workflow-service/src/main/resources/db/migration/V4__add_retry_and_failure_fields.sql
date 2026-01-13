ALTER TABLE workflow.workflow_step_execution
    ADD COLUMN retry_count INT NOT NULL DEFAULT 0
