DROP TABLE IF EXISTS users, items, bookings, comments, requests;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(250) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500) NOT NULL,
    available BOOLEAN,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT FK_ITEM_FOR_OWNER FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(25) NOT NULL,
    CONSTRAINT FK_BOOKING_FOR_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT FK_BOOKING_FOR_BOOKER FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    text VARCHAR(1024) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITH TIME ZONE,
    CONSTRAINT FK_COMMENTS_FOR_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT FK_COMMENTS_FOR_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT generated BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITH TIME ZONE,
    CONSTRAINT FK_ITEM_REQUEST_FOR_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);