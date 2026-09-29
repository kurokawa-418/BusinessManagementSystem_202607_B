CREATE TABLE IF NOT EXISTS m_client
(
   `client_id` INT (11) NOT NULL,
   `client_name` VARCHAR (50) NOT NULL,
   `open_time` TIME NOT NULL,
   `close_time` TIME NOT NULL,
   `working_time` DECIMAL
   (
      5,
      2
   )
   NOT NULL,
   `rest1_start` TIME NOT NULL,
   `rest1_end` TIME NOT NULL,
   `rest2_start` TIME NULL DEFAULT NULL,
   `rest2_end` TIME NULL DEFAULT NULL,
   `rest3_start` TIME NULL DEFAULT NULL,
   `rest3_end` TIME NULL DEFAULT NULL,
   `rest4_start` TIME NULL DEFAULT NULL,
   `rest4_end` TIME NULL DEFAULT NULL,
   `rest5_start` TIME NULL DEFAULT NULL,
   `rest5_end` TIME NULL DEFAULT NULL,
   `rest6_start` TIME NULL DEFAULT NULL,
   `rest6_end` TIME NULL DEFAULT NULL,
   `adjust_rest_time_start` TIME NULL DEFAULT NULL,
   `adjust_rest_time_end` TIME NULL DEFAULT NULL,
   `comment` VARCHAR (100) NULL DEFAULT NULL,
   `delete_flg` boolean NULL DEFAULT NULL,
   `created_at` DATETIME NULL DEFAULT NULL,
   `created_user` VARCHAR (16) NULL DEFAULT NULL,
   `updated_at` DATETIME NULL DEFAULT NULL,
   `updated_user` VARCHAR (16) NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS m_user
(
    seq_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(16) NOT NULL UNIQUE,
    user_name VARCHAR(16) NOT NULL,
    password VARCHAR(64) NOT NULL,
    auth_id INT NOT NULL,
    mail_address VARCHAR(254) NOT NULL,
    delete_flg BOOLEAN,
    created_at DATETIME,
    created_user VARCHAR(16),
    updated_at DATETIME,
    updated_user VARCHAR(16)
);

CREATE TABLE IF NOT EXISTS m_authority
(
    auth_id INT PRIMARY KEY,
    auth_status VARCHAR(10) NOT NULL UNIQUE
);