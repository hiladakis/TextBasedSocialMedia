create table giannis.registered_users
(
    id                  integer generated always as identity (START 1 increment 1) primary key,
    username            varchar(255) unique     not null,
    password            varchar(255)            not null,
    role                varchar(7)              not null,
    version             integer
);

create table giannis.authenticated_users
(
    id                  integer generated always as identity (START 1 increment 1) primary key,
    username            varchar(255)            not null,
    role                varchar(7)              not null,
    login_date          timestamp               not null,
    version             integer
);

create table giannis.user_posts
(
    id                  integer generated always as identity (START 1 increment 1) primary key,
    text                varchar(3000)           not null,
    user_id             integer,
    post_date           timestamp               not null,
    version             integer,
    CONSTRAINT          fk_user_id
        FOREIGN KEY         (user_id) REFERENCES giannis.registered_users(id) ON DELETE CASCADE
);

create table giannis.post_comments
(
    id                  integer generated always as identity (START 1 increment 1) primary key,
    comment             varchar(3000)           not null,
    post_id             integer,
    comment_date        timestamp               not null,
    comment_user        varchar(255)            not null,
    version             integer,
    CONSTRAINT          fk_post_id
        FOREIGN KEY         (post_id) REFERENCES giannis.user_posts(id) ON DELETE CASCADE
);

create table giannis.followers
(
    id                  integer generated always as identity (START 1 increment 1) primary key,
    follower_user_id    integer,
    followed_user_id    integer,
    version             integer,
    UNIQUE(follower_user_id, followed_user_id),
    CONSTRAINT          fk_follower_user_id
        FOREIGN KEY         (follower_user_id) REFERENCES giannis.registered_users(id) ON DELETE CASCADE,
    CONSTRAINT          fk_followed_user_id
        FOREIGN KEY         (followed_user_id) REFERENCES giannis.registered_users(id) ON DELETE CASCADE
);