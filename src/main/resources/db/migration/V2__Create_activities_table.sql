-- V2__Create_activities_table.sql

CREATE TABLE activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    category_id BIGINT NOT NULL,
    location VARCHAR(500) NOT NULL,
    price DECIMAL(10,2),
    min_participants INTEGER,
    max_participants INTEGER,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activities_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX idx_activities_category ON activities(category_id);
CREATE INDEX idx_activities_active ON activities(is_active);
CREATE INDEX idx_activities_location ON activities(location);
CREATE INDEX idx_activities_time ON activities(start_time, end_time);
