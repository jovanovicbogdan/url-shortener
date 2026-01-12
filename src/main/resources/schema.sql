DROP TABLE IF EXISTS url;

CREATE TABLE url (
    url_id BIGSERIAL PRIMARY KEY,
    original_url TEXT NOT NULL,
    short_url VARCHAR(255) NOT NULL UNIQUE
);

CREATE INDEX idx_url_original_url ON url (original_url);
