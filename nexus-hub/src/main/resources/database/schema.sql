CREATE TABLE IF NOT EXISTS `jumps_records` (
    `jump_name`   VARCHAR(255) NOT NULL,
    `player_uuid`  VARCHAR(36) NOT NULL,
    `time`  INT(11) NOT NULL,
    PRIMARY KEY (`jump_name`, `player_uuid`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;;