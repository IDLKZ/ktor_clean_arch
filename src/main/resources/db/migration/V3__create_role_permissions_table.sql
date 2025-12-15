-- Создание таблицы связи ролей и прав доступа (role_permissions)
-- Реализует отношение многие-ко-многим между ролями и правами

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,

    -- Составной первичный ключ
    PRIMARY KEY (role_id, permission_id),

    -- Внешние ключи с каскадным удалением
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE role_permissions IS 'Таблица связи ролей и прав доступа (many-to-many)';
COMMENT ON COLUMN role_permissions.role_id IS 'Идентификатор роли';
COMMENT ON COLUMN role_permissions.permission_id IS 'Идентификатор права доступа';
