-- V1__Create_categories_table.sql

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    emoji VARCHAR(10),
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_name ON categories(name);
CREATE INDEX idx_categories_active ON categories(is_active);

-- Insert default categories
INSERT INTO categories (name, emoji, description, created_at) VALUES
('Квесты', '🧩', 'Комнаты-квесты, escape rooms', CURRENT_TIMESTAMP),
('Футбол', '⚽', 'Футбольные матчи, аренда полей', CURRENT_TIMESTAMP),
('Ивенты', '🎪', 'Концерты, выставки, фестивали', CURRENT_TIMESTAMP),
('Спорт', '🏃', 'Спортивные мероприятия', CURRENT_TIMESTAMP),
('Воркшопы', '🔧', 'Мастер-классы, обучение', CURRENT_TIMESTAMP),
('Игры', '🎮', 'Настольные игры, киберспорт', CURRENT_TIMESTAMP);
