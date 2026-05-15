CREATE TABLE rules (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(30)  NOT NULL,
    value       INTEGER      NOT NULL CHECK (value > 0),
    period      VARCHAR(10)  NOT NULL,
    description VARCHAR(500)
);

INSERT INTO rules (id, name, type, value, period, description) VALUES
    ('11111111-0000-0000-0000-000000000001', 'Maksymalny czas pracy miesięczny',
        'MAX_WORK_TIME', 100, 'MONTH',
        'Pilot nie może przekroczyć 100 godzin pracy w miesiącu'),
    ('11111111-0000-0000-0000-000000000002', 'Maksymalny czas pracy dzienny',
        'MAX_WORK_TIME', 10, 'DAY',
        'Pilot nie może przekroczyć 10 godzin pracy dziennie'),
    ('11111111-0000-0000-0000-000000000003', 'Minimalny odpoczynek między lotami',
        'MIN_REST_TIME', 12, 'DAY',
        'Wymagany minimalny odpoczynek 12 godzin między lotami'),
    ('11111111-0000-0000-0000-000000000004', 'Minimalny nalot miesięczny',
        'MIN_FLIGHT_TIME', 10, 'MONTH',
        'Pilot musi wykonać minimum 10 godzin nalotu miesięcznie');
