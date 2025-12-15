-- Добавление поля id к таблице role_permissions
-- Изменение структуры с составного ключа на суррогатный ключ id

-- Шаг 1: Удаляем старый составной первичный ключ
ALTER TABLE role_permissions DROP CONSTRAINT role_permissions_pkey;

-- Шаг 2: Добавляем новое поле id как BIGSERIAL (auto-increment)
ALTER TABLE role_permissions ADD COLUMN id BIGSERIAL;

-- Шаг 3: Устанавливаем id как новый первичный ключ
ALTER TABLE role_permissions ADD PRIMARY KEY (id);

-- Шаг 4: Добавляем уникальный индекс на пару (role_id, permission_id)
-- чтобы предотвратить дубликаты связей
ALTER TABLE role_permissions
    ADD CONSTRAINT uk_role_permissions_role_permission
    UNIQUE (role_id, permission_id);

-- Комментарии
COMMENT ON COLUMN role_permissions.id IS 'Уникальный идентификатор записи связи';
