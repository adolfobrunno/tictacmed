-- Table to store inbound WhatsApp messages
CREATE TABLE IF NOT EXISTS whatsapp_messages
(
    id           VARCHAR(100) PRIMARY KEY,
    from_number  VARCHAR(50)  NOT NULL,
    body         TEXT         NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    received_at  TIMESTAMP(3) NOT NULL,
    processed_at TIMESTAMP(3) NULL
) ENGINE = InnoDB;
