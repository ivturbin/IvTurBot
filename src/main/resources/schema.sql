GRANT ALL ON SCHEMA PUBLIC TO bot_user;

CREATE TABLE IF NOT EXISTS highday (
    highday_id bigserial PRIMARY KEY,
    tg_chat_id BIGINT,
    description TEXT,
    HIGHDAY_TYPE TEXT,
    highday_dt DATE NOT NULL
    );

CREATE TABLE IF NOT EXISTS recipient (
    recipient_id bigserial PRIMARY KEY,
    tg_chat_id BIGINT NOT NULL,
    notification_type text NOT NULL,
    recipient_type text NOT NULL
    );

CREATE TABLE IF NOT EXISTS highday_recipient (
    recipient_id BIGINT NOT NULL,
    highday_id BIGINT NOT NULL,
    PRIMARY KEY (recipient_id, highday_id),
    FOREIGN KEY (recipient_id) REFERENCES recipient(recipient_id),
    FOREIGN KEY (highday_id) REFERENCES highday(highday_id)
    );