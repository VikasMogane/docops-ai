CREATE TABLE public.documents (
    id BIGSERIAL PRIMARY KEY,
    org_id BIGINT NOT NULL,
    uploaded_by BIGINT NOT NULL,
    file_name TEXT NOT NULL,
    content_type TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL
);


CREATE INDEX idx_documents_org ON documents(org_id);
