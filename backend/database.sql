DROP DATABASE IF EXISTS coreWallet;

CREATE DATABASE coreWallet;

USE coreWallet;

CREATE TABLE
    `user` (
        `id_user` INT (11) AUTO_INCREMENT NOT NULL,
        `username` VARCHAR(16) NOT NULL UNIQUE,
        `password` VARCHAR(64) NOT NULL,
        `pin` CHAR(6) NOT NULL,
        `email` VARCHAR(64) NOT NULL UNIQUE,
        `name` VARCHAR(64) NOT NULL,
        `account_number` VARCHAR(16) NOT NULL UNIQUE,
        `balance` BIGINT (20) DEFAULT 100000,
        `image_path` VARCHAR(255) DEFAULT 'default_image_path',
        PRIMARY KEY (`id_user`)
    );

CREATE TABLE
    `contact` (
        `id_user` INT (11) NOT NULL,
        `saved_id_user` INT (11) NOT NULL,
        `datetime` DATETIME DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`id_user`, `saved_id_user`),
        FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
        FOREIGN KEY (`saved_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE
    );

CREATE TABLE
    `transaction_category` (
        `id_category` INT (11) AUTO_INCREMENT NOT NULL,
        `id_user` INT (11) DEFAULT NULL,
        `category_name` VARCHAR(32) NOT NULL,
        PRIMARY KEY (`id_category`),
        FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
        UNIQUE (`id_user`, `category_name`)
    );

CREATE TABLE
    `transaction` (
        `id_transaction` INT (11) AUTO_INCREMENT NOT NULL,
        `datetime` DATETIME DEFAULT CURRENT_TIMESTAMP,
        `source_id_user` INT (11) NOT NULL,
        `target_id_user` INT (11) DEFAULT NULL,
        `id_category` INT (11) NOT NULL,
        `transaction_type` ENUM ('Top-Up', 'Transfer') NOT NULL,
        `status` TINYINT (1) DEFAULT 0, 
        `amount` BIGINT (20) NOT NULL,
        `note` VARCHAR(64) DEFAULT '',
        PRIMARY KEY (`id_transaction`),
        FOREIGN KEY (`source_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
        FOREIGN KEY (`target_id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
        FOREIGN KEY (`id_category`) REFERENCES `transaction_category` (`id_category`)
    );

CREATE TABLE
    `log_login` (
        `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
        `id_user` INT (11),
        `name` VARCHAR(64),
        `user_agent` VARCHAR(255),
        `note` VARCHAR(64),
        FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE
    );

CREATE TABLE
    `budget_plan` (
        `id_budget` INT (11) AUTO_INCREMENT NOT NULL,
        `id_user` INT (11) NOT NULL,
        `plan_name` VARCHAR(64) NOT NULL,
        `id_category` INT (11) NOT NULL,
        `amount_limit` BIGINT (20) NOT NULL,
        `spent_amount` BIGINT (20) DEFAULT 0,
        `start_date` DATE NOT NULL,
        `end_date` DATE NOT NULL,
        `status` TINYINT (1) DEFAULT 0, 
        PRIMARY KEY (`id_budget`),
        FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE,
        FOREIGN KEY (`id_category`) REFERENCES `transaction_category` (`id_category`) ON DELETE CASCADE
    );

CREATE TABLE
    `budget_goal` (
        `id_goal` INT (11) AUTO_INCREMENT NOT NULL,
        `id_user` INT (11) NOT NULL,
        `goal_name` VARCHAR(64) NOT NULL,
        `target_amount` BIGINT (20) NOT NULL,
        `saved_amount` BIGINT (20) DEFAULT 0,
        `deadline` DATE NOT NULL,
        `status` TINYINT (1) DEFAULT 0, 
        PRIMARY KEY (`id_goal`),
        FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE
    );

CREATE TABLE
    `notifications` (
        `id_notification` INT (11) AUTO_INCREMENT NOT NULL,
        `id_user` INT (11) NOT NULL,
        `message` TEXT NOT NULL,
        `is_read` TINYINT (1) DEFAULT 0,
        `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (`id_notification`),
        FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE
    );

INSERT INTO
    transaction_category (id_user, category_name)
VALUES
    (NULL, 'Top-Up'),
    (NULL, 'Food'),
    (NULL, 'Shopping'),
    (NULL, 'Entertainment'),
    (NULL, 'Bills'),
    (NULL, 'Transfer'),
    (NULL, 'Other/Miscellaneous');