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

CREATE TABLE IF NOT EXISTS m_employee
(
    `employee_id` int(4) NOT NULL COMMENT '社員ID',
    `employee_name` varchar(16) NOT NULL COMMENT '社員氏名',
    `client_id` int(4) NOT NULL COMMENT '顧客ID',
    `hourly_wage` bit(1) DEFAULT NULL COMMENT '時給',
    `paid_holiday_std` date DEFAULT NULL COMMENT '有休基準日',
    `delete_flg` bit(1) DEFAULT NULL COMMENT '削除フラグ',
    `created_at` datetime DEFAULT NULL COMMENT 'レコード作成日付',
    `created_user` varchar(16) DEFAULT NULL COMMENT 'レコード作成ユーザID',
    `updated_at` datetime DEFAULT NULL COMMENT 'レコード最終更新日付',
    `updated_user` varchar(16) DEFAULT NULL COMMENT 'レコード最終更新ユーザID',
    PRIMARY KEY (`employee_id`),
    UNIQUE KEY `UQ_EMPLOYEE_NAME` (`employee_name`),
    KEY `client_id` (`client_id`),
    CONSTRAINT `m_employee_ibfk_1`
        FOREIGN KEY (`client_id`) REFERENCES `m_client` (`client_id`)
);

CREATE TABLE IF NOT EXISTS m_employee_paid_vacation
(
    `seq_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'シーケンスID',
    `employee_id` int(4) NOT NULL COMMENT '社員ID',
    `year` date DEFAULT NULL COMMENT '年度',
    `remaind_this_year` decimal(10,0) DEFAULT NULL COMMENT '有給保有数-当年度分',
    `remaind_last_year` decimal(10,0) DEFAULT NULL COMMENT '有給保有数-前年度分',
    `delete_flg` bit(1) DEFAULT NULL COMMENT '削除フラグ',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード作成日付',
    `created_user` varchar(11) DEFAULT NULL COMMENT 'レコード作成ユーザID',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'レコード最終日付',
    `updated_user` varchar(11) DEFAULT NULL COMMENT 'レコード最終更新日付',
    PRIMARY KEY (`seq_id`),
    KEY `employee_id` (`employee_id`),
    CONSTRAINT `m_employee_paid_vacation_ibfk_1`
        FOREIGN KEY (`employee_id`) REFERENCES `m_employee` (`employee_id`)
);