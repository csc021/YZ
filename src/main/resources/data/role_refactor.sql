-- Role refactor migration for ks-1
ALTER TABLE user
    ADD COLUMN username VARCHAR(64) COMMENT 'courier or station master login username';

ALTER TABLE user
    ADD COLUMN email VARCHAR(128) COMMENT 'email';

ALTER TABLE user
    ADD COLUMN employee_no VARCHAR(32) COMMENT 'courier employee number';

ALTER TABLE station
    ADD COLUMN brand VARCHAR(32) COMMENT 'station brand';

ALTER TABLE parcel
    MODIFY COLUMN shelf_id BIGINT NULL COMMENT 'shelf id can be null';

CREATE TABLE IF NOT EXISTS mail_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    sender_name VARCHAR(64),
    sender_phone VARCHAR(32),
    sender_province VARCHAR(64),
    sender_city VARCHAR(64),
    sender_district VARCHAR(64),
    sender_address VARCHAR(255),
    receiver_name VARCHAR(64),
    receiver_phone VARCHAR(32),
    receiver_province VARCHAR(64),
    receiver_city VARCHAR(64),
    receiver_district VARCHAR(64),
    receiver_address VARCHAR(255),
    item_name VARCHAR(128),
    item_type VARCHAR(64),
    item_weight DECIMAL(10,2),
    carrier_id BIGINT,
    station_id BIGINT,
    status INT,
    remark VARCHAR(255),
    created_at VARCHAR(32),
    updated_at VARCHAR(32)
);
