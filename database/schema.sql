CREATE TABLE IF NOT EXISTS artists (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    country VARCHAR(20),
    albums_fetched BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS albums (
    id VARCHAR(36) PRIMARY KEY,
    artist_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    first_release_date VARCHAR(10),
    primary_type VARCHAR(50),
    secondary_types TEXT[],

    FOREIGN KEY (artist_id) REFERENCES artists(id)
);