CREATE TABLE download_record (
     record_id BIGINT PRIMARY KEY,
     user_id BIGINT,
     enterprise_id BIGINT,
     business_type INT,
     file_name VARCHAR(64),
     file_suffix VARCHAR(32),
     file_status TINYINT,
     file_download_url VARCHAR(100),
     request_body VARCHAR(100),
     remark VARCHAR(32),
     create_time DATETIME,
     update_time DATETIME
);
