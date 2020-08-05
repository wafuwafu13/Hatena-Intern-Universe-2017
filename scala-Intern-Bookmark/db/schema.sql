CREATE TABLE user (
    `id` BIGINT UNSIGNED NOT NULL,

    `name` VARBINARY(32) NOT NULL,

    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY (name),

    KEY (created_at),
    KEY (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE entry (
    `id` BIGINT UNSIGNED NOT NULL,

    `url` VARBINARY(512) NOT NULL,
    `title` VARCHAR(512) NOT NULL,

    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY (url(191))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE bookmark (
    `id` BIGINT UNSIGNED NOT NULL,

    `user_id` BIGINT UNSIGNED NOT NULL,
    `entry_id` BIGINT UNSIGNED NOT NULL,
    `comment` VARCHAR(256) NOT NULL,

    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY (user_id, entry_id),
    KEY (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
