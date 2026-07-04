-- USER table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    role VARCHAR(20) NOT NULL DEFAULT 'USER',

    CONSTRAINT chk_users_role
        CHECK (role IN ('USER', 'REPORTER', 'ADMIN'))
);

-- PROFILES table
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,

    first_name VARCHAR(100),
    last_name VARCHAR(100),
    headline VARCHAR(150),
    bio TEXT,
    programming_languages VARCHAR(255),
    github_link VARCHAR(255),
    linkedin_link VARCHAR(255),
    profile_image_url VARCHAR(500),

    CONSTRAINT fk_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- MESSAGES table
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

-- FRIEND_REQUESTS table
CREATE TABLE friend_request (
    id BIGSERIAL PRIMARY KEY,

    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,

    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_friend_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_friend_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

ALTER TABLE friend_request
ADD CONSTRAINT unique_friend_pair
UNIQUE (sender_id, receiver_id);

-- POSTS table
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    language_tag VARCHAR(255),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_posts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_posts_visibility
            CHECK (visibility IN ('PUBLIC', 'PRIVATE'))
);

CREATE INDEX idx_posts_created_at
ON posts (created_at DESC);

CREATE INDEX idx_posts_user_id
ON posts (user_id);

CREATE INDEX idx_posts_visibility
ON posts (visibility);

CREATE INDEX idx_posts_language_tag
ON posts (language_tag);

-- POST_LIKED table

CREATE TABLE post_likes (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_post_likes_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_likes_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_post_likes_post_user
        UNIQUE (post_id, user_id)
);

CREATE INDEX idx_post_likes_post_id
ON post_likes (post_id);

CREATE INDEX idx_post_likes_user_id
ON post_likes (user_id);

-- Comment

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_by_post_author BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_comments_post_id
ON comments (post_id);

CREATE INDEX idx_comments_user_id
ON comments (user_id);

CREATE INDEX idx_comments_created_at
ON comments (created_at);

-- REPORTS table

CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,

    reporter_id BIGINT NOT NULL,

    reported_user_id BIGINT,
    reported_post_id BIGINT,
    reported_comment_id BIGINT,

    target_type VARCHAR(20) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    details TEXT,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    created_at TIMESTAMP NOT NULL,
    reviewed_by_id BIGINT,
    reviewed_at TIMESTAMP,
    conclusion TEXT,
    action_taken TEXT,
    case_id BIGINT,

    CONSTRAINT fk_reports_reporter
        FOREIGN KEY (reporter_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reports_reported_user
        FOREIGN KEY (reported_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reports_reported_post
        FOREIGN KEY (reported_post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reports_reported_comment
        FOREIGN KEY (reported_comment_id)
        REFERENCES comments(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reports_reviewed_by
        FOREIGN KEY (reviewed_by_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_reports_case
    FOREIGN KEY (case_id)
    REFERENCES user_cases(id)
    ON DELETE SET NULL,

    CONSTRAINT chk_reports_target_type
        CHECK (target_type IN ('USER', 'POST', 'COMMENT')),

    CONSTRAINT chk_reports_status
        CHECK (status IN ('PENDING', 'REVIEWED', 'DISMISSED', 'ACTION_TAKEN')),

    CONSTRAINT chk_reports_target_exists
        CHECK (
            (target_type = 'USER' AND reported_user_id IS NOT NULL AND reported_post_id IS NULL AND reported_comment_id IS NULL)
            OR
            (target_type = 'POST' AND reported_post_id IS NOT NULL AND reported_user_id IS NULL AND reported_comment_id IS NULL)
            OR
            (target_type = 'COMMENT' AND reported_comment_id IS NOT NULL AND reported_user_id IS NULL AND reported_post_id IS NULL)
        )


);

-- NOTIFICATIONS table

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,

    receiver_id BIGINT NOT NULL,
    actor_id BIGINT,

    type VARCHAR(40) NOT NULL,
    message TEXT NOT NULL,

    post_id BIGINT,
    comment_id BIGINT,
    friend_request_id BIGINT,

    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_notifications_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notifications_actor
        FOREIGN KEY (actor_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_notifications_post
        FOREIGN KEY (post_id)
        REFERENCES posts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notifications_comment
        FOREIGN KEY (comment_id)
        REFERENCES comments(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_notifications_friend_request
        FOREIGN KEY (friend_request_id)
        REFERENCES friend_request(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_notifications_type
        CHECK (type IN (
            'FRIEND_REQUEST',
            'FRIEND_POST',
            'POST_COMMENT',
            'POST_LIKE',
            'COMMENT_DELETED'
        ))
);

CREATE INDEX idx_notifications_receiver_hidden_created
ON notifications (receiver_id, hidden, created_at DESC);

CREATE INDEX idx_notifications_type
ON notifications (type);

-- NOTIFICATION_SETTINGS table

CREATE TABLE notification_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,

    notify_friend_posts BOOLEAN NOT NULL DEFAULT TRUE,
    notify_post_comments BOOLEAN NOT NULL DEFAULT TRUE,
    notify_post_likes BOOLEAN NOT NULL DEFAULT TRUE,
    notify_comment_deleted BOOLEAN NOT NULL DEFAULT TRUE,
    notify_friend_requests BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_notification_settings_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- USER_CASES

CREATE TABLE user_cases (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL UNIQUE,

    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_user_cases_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_cases_status
        CHECK (status IN ('OPEN', 'CLOSED'))
);

-- BLOCK_REQUESTS

CREATE TABLE block_requests (
    id BIGSERIAL PRIMARY KEY,

    user_case_id BIGINT NOT NULL,
    requested_by_id BIGINT NOT NULL,

    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    admin_decision TEXT,
    decided_by_id BIGINT,
    decided_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_block_requests_case
        FOREIGN KEY (user_case_id)
        REFERENCES user_cases(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_block_requests_requested_by
        FOREIGN KEY (requested_by_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_block_requests_decided_by
        FOREIGN KEY (decided_by_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_block_requests_status
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- USER_BLOCKS table

CREATE TABLE user_blocks (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    blocked_by_id BIGINT NOT NULL,
    block_request_id BIGINT,

    reason TEXT NOT NULL,
    blocked_at TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_user_blocks_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_blocks_blocked_by
        FOREIGN KEY (blocked_by_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_blocks_request
        FOREIGN KEY (block_request_id)
        REFERENCES block_requests(id)
        ON DELETE SET NULL
);