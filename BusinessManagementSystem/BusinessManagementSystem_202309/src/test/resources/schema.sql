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