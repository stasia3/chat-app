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
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_posts_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_posts_created_at
ON posts (created_at DESC);

CREATE INDEX idx_posts_user_id
ON posts (user_id);