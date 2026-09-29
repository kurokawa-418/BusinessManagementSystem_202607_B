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
    `employee_id` int(4) NOT NULL,
    `employee_name` varchar(16) NOT NULL,
    `client_id` int(4) NOT NULL,
    `hourly_wage` bit(1) DEFAULT NULL,
    `paid_holiday_std` date DEFAULT NULL,
    `delete_flg` bit(1) DEFAULT NULL,
    `created_at` datetime DEFAULT NULL,
    `created_user` varchar(16) DEFAULT NULL,
    `updated_at` datetime DEFAULT NULL,
    `updated_user` varchar(16) DEFAULT NULL,
    PRIMARY KEY (`employee_id`),
    UNIQUE KEY `UQ_EMPLOYEE_NAME` (`employee_name`),
    KEY `client_id` (`client_id`),
    CONSTRAINT `m_employee_ibfk_1`
        FOREIGN KEY (`client_id`) REFERENCES `m_client` (`client_id`)
);

CREATE TABLE IF NOT EXISTS m_employee_paid_vacation
(
    `seq_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
    `employee_id` int(4) NOT NULL,
    "year" date DEFAULT NULL,
    `remaind_this_year` decimal(10,0) DEFAULT NULL,
    `remaind_last_year` decimal(10,0) DEFAULT NULL,
    `delete_flg` bit(1) DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `created_user` varchar(11) DEFAULT NULL,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_user` varchar(11) DEFAULT NULL,
    PRIMARY KEY (`seq_id`),
    KEY `employee_id` (`employee_id`),
    CONSTRAINT `m_employee_paid_vacation_ibfk_1`
        FOREIGN KEY (`employee_id`) REFERENCES `m_employee` (`employee_id`)
);