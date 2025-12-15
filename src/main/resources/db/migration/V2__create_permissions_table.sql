-- Создание таблицы прав доступа (permissions)
-- Поддерживает многоязычность (RU, KK, EN) и soft delete

CREATE TABLE permissions (
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
CREATE INDEX idx_permissions_value ON permissions(value);
CREATE INDEX idx_permissions_system ON permissions(system);
CREATE INDEX idx_permissions_administrative ON permissions(administrative);
CREATE INDEX idx_permissions_deleted_at ON permissions(deleted_at);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE permissions IS 'Таблица прав доступа с поддержкой многоязычности';
COMMENT ON COLUMN permissions.id IS 'Уникальный идентификатор права доступа';
COMMENT ON COLUMN permissions.title_ru IS 'Название права на русском языке';
COMMENT ON COLUMN permissions.title_kk IS 'Название права на казахском языке';
COMMENT ON COLUMN permissions.title_en IS 'Название права на английском языке';
COMMENT ON COLUMN permissions.value IS 'Уникальное значение права (например: READ_USERS, WRITE_POSTS)';
COMMENT ON COLUMN permissions.system IS 'Системное право (не может быть удалено)';
COMMENT ON COLUMN permissions.administrative IS 'Административное право';
COMMENT ON COLUMN permissions.created_at IS 'Дата и время создания записи';
COMMENT ON COLUMN permissions.updated_at IS 'Дата и время последнего обновления';
COMMENT ON COLUMN permissions.deleted_at IS 'Дата и время удаления (soft delete)';
