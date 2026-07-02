-- USER table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
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