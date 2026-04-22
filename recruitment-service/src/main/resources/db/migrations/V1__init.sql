-- Включаем расширение для генерации UUID (если ещё не включено)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================
-- Vacancy
-- =========================
CREATE TABLE vacancy (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    description TEXT,

    formats TEXT[] NOT NULL DEFAULT '{}',
    tags TEXT[] NOT NULL DEFAULT '{}',

    salary_from INTEGER,
    salary_to INTEGER,

    created_by UUID NOT NULL,
    available_for UUID[] NOT NULL DEFAULT '{}',

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

-- Индексы (чтобы потом не страдать)
CREATE INDEX idx_vacancy_created_by ON vacancy(created_by);
CREATE INDEX idx_vacancy_salary ON vacancy(salary_from, salary_to);

-- GIN индекс для массивов (поиск по тегам/форматам)
CREATE INDEX idx_vacancy_tags_gin ON vacancy USING GIN (tags);
CREATE INDEX idx_vacancy_formats_gin ON vacancy USING GIN (formats);


-- =========================
-- Application
-- =========================
CREATE TABLE application (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    vacancy_id UUID NOT NULL,

    first_name VARCHAR(52) NOT NULL,
    last_name VARCHAR(52) NOT NULL,
    patronymic VARCHAR(52),

    resume_text TEXT,
    cover_letter TEXT,
    expected_salary INTEGER,

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_application_vacancy
        FOREIGN KEY (vacancy_id)
        REFERENCES vacancy(id)
        ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_application_vacancy_id ON application(vacancy_id);