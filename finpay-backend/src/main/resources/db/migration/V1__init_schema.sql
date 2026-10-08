CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) DEFAULT NULL,
    `name` VARCHAR(255) DEFAULT NULL,
    `password` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `wallet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `balance` DECIMAL(38,2) DEFAULT NULL,
    `user_id` BIGINT DEFAULT NULL,
    `version` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UKhgee4p1hiwadqinr0avxlq4eb` (`user_id`),
    CONSTRAINT `FKbs4ogwiknsup4rpw8d47qw9dx`
        FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `transaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `amount` DECIMAL(38,2) DEFAULT NULL,
    `status` VARCHAR(255) DEFAULT NULL,
    `receiver_id` BIGINT DEFAULT NULL,
    `sender_id` BIGINT DEFAULT NULL,
    `created_at` DATETIME(6) DEFAULT NULL,
    `idempotency_key` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FKey21a233t8tlwfsbs228q3b2u` (`receiver_id`),
    KEY `FKjpter5yuohdb58gyg6k5nympt` (`sender_id`),
    CONSTRAINT `FKey21a233t8tlwfsbs228q3b2u`
        FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`),
    CONSTRAINT `FKjpter5yuohdb58gyg6k5nympt`
        FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci;