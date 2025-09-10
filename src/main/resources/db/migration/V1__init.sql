-- Initial schema for patients, medication schedules, and administration records
CREATE TABLE IF NOT EXISTS patient
(
    id      BINARY(16) PRIMARY KEY,
    name    VARCHAR(200) NOT NULL,
    contact VARCHAR(255) NOT NULL
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS medication_schedule
(
    id                BINARY(16) PRIMARY KEY,
    patient_id        BINARY(16)   NOT NULL,
    medicine_name     VARCHAR(200) NOT NULL,
    start_at          TIMESTAMP(3) NOT NULL,
    end_at            TIMESTAMP(3) NOT NULL,
    frequency_seconds BIGINT       NOT NULL,
    CONSTRAINT fk_schedule_patient FOREIGN KEY (patient_id) REFERENCES patient (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS administration_record
(
    id           BINARY(16) PRIMARY KEY,
    schedule_id  BINARY(16)   NOT NULL,
    scheduled_at TIMESTAMP(3) NOT NULL,
    confirmed_at TIMESTAMP(3) NOT NULL,
    CONSTRAINT fk_record_schedule FOREIGN KEY (schedule_id) REFERENCES medication_schedule (id),
    CONSTRAINT uq_schedule_scheduled_at UNIQUE (schedule_id, scheduled_at)
) ENGINE = InnoDB;
