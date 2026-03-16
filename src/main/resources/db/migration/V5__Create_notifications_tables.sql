-- V5__Create_notifications_tables.sql

-- User notification settings
CREATE TABLE user_notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_telegram_id BIGINT NOT NULL UNIQUE,
    remind_24h BOOLEAN DEFAULT TRUE,
    remind_1h BOOLEAN DEFAULT TRUE,
    remind_custom_hours INTEGER,
    email_notifications BOOLEAN DEFAULT FALSE,
    email VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_notification_settings_telegram ON user_notification_settings(user_telegram_id);

-- Notifications table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_telegram_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    booking_id BIGINT,
    notification_type VARCHAR(50) NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    sent_time TIMESTAMP,
    status VARCHAR(50) DEFAULT 'SCHEDULED' NOT NULL,
    message VARCHAR(2000),
    error_message VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_activity FOREIGN KEY (activity_id) REFERENCES activities(id),
    CONSTRAINT fk_notifications_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_notifications_status_time ON notifications(status, scheduled_time);
CREATE INDEX idx_notifications_user ON notifications(user_telegram_id, status);
CREATE INDEX idx_notifications_activity ON notifications(activity_id, status);
