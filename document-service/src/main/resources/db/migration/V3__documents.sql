CREATE TABLE public.document_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    content TEXT NOT NULL
);

CREATE INDEX idx_chunks_document ON public.document_chunks(document_id);
