-- V4__Create_bookings_table.sql

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    user_telegram_id BIGINT NOT NULL,
    user_name VARCHAR(200),
    activity_id BIGINT NOT NULL,
    participants_count INTEGER DEFAULT 1,
    status VARCHAR(50) DEFAULT 'CONFIRMED' NOT NULL,
    total_price DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_activity FOREIGN KEY (activity_id) REFERENCES activities(id)
);

CREATE INDEX idx_bookings_user_telegram ON bookings(user_telegram_id);
CREATE INDEX idx_bookings_activity ON bookings(activity_id);
CREATE INDEX idx_bookings_status ON bookings(status);
