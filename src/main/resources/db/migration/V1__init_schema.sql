CREATE TABLE workers (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL,

    phone VARCHAR(20) NOT NULL UNIQUE,

    designation VARCHAR(30) NOT NULL,

    daily_wage_rate NUMERIC(10,2) NOT NULL CHECK (daily_wage_rate >= 0),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workers_phone
ON workers(phone);





CREATE TABLE sites (
    id BIGSERIAL PRIMARY KEY,

    site_name VARCHAR(150) NOT NULL,

    location VARCHAR(255) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);





CREATE TABLE attendance_logs (
    id BIGSERIAL PRIMARY KEY,

    worker_id BIGINT NOT NULL,

    site_id BIGINT NOT NULL,

    clock_in TIMESTAMP NOT NULL,

    clock_out TIMESTAMP,

    total_hours NUMERIC(5,2),

    overtime_hours NUMERIC(5,2),

    flagged BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attendance_worker
        FOREIGN KEY (worker_id)
        REFERENCES workers(id),

    CONSTRAINT fk_attendance_site
        FOREIGN KEY (site_id)
        REFERENCES sites(id)
);

CREATE INDEX idx_attendance_worker
ON attendance_logs(worker_id);

CREATE INDEX idx_attendance_clockin
ON attendance_logs(clock_in);

CREATE INDEX idx_active_attendance
ON attendance_logs(worker_id, clock_out);





CREATE TABLE overtime_entries (
    id BIGSERIAL PRIMARY KEY,

    worker_id BIGINT NOT NULL,

    attendance_id BIGINT NOT NULL,

    overtime_date DATE NOT NULL,

    overtime_hours NUMERIC(5,2) NOT NULL CHECK (overtime_hours >= 0),

    overtime_rate_applied NUMERIC(10,2) NOT NULL,

    amount NUMERIC(10,2) NOT NULL,

    settlement_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_overtime_worker
        FOREIGN KEY (worker_id)
        REFERENCES workers(id),

    CONSTRAINT fk_overtime_attendance
        FOREIGN KEY (attendance_id)
        REFERENCES attendance_logs(id)
);

CREATE INDEX idx_overtime_worker_month
ON overtime_entries(worker_id, overtime_date);

CREATE INDEX idx_overtime_status
ON overtime_entries(settlement_status);