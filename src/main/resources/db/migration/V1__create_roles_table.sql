-- Создание таблицы ролей (roles)
-- Поддерживает многоязычность (RU, KK, EN) и soft delete

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    title_ru VARCHAR(255) NOT NULL,
    title_kk VARCHAR(255),
    title_en VARCHAR(255),
    description_ru TEXT,
    description_kk TEXT,
    description_en TEXT,
    value VARCHAR(255) NOT NULL UNIQUE,
    system BOOLEAN NOT NULL DEFAULT FALSE,
    administrative BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_roles_value ON roles(value);
CREATE INDEX idx_roles_system ON roles(system);
CREATE INDEX idx_roles_administrative ON roles(administrative);
CREATE INDEX idx_roles_deleted_at ON roles(deleted_at);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE roles IS 'Таблица ролей пользователей с поддержкой многоязычности';
COMMENT ON COLUMN roles.id IS 'Уникальный идентификатор роли';
COMMENT ON COLUMN roles.title_ru IS 'Название роли на русском языке';
COMMENT ON COLUMN roles.title_kk IS 'Название роли на казахском языке';
COMMENT ON COLUMN roles.title_en IS 'Название роли на английском языке';
COMMENT ON COLUMN roles.value IS 'Уникальное значение роли (например: ADMIN, USER)';
COMMENT ON COLUMN roles.system IS 'Системная роль (не может быть удалена)';
COMMENT ON COLUMN roles.administrative IS 'Административная роль';
COMMENT ON COLUMN roles.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN roles.updated_at IS 'Дата и время последнего обновления';
COMMENT ON COLUMN roles.deleted_at IS 'Дата и время удаления (soft delete)';
