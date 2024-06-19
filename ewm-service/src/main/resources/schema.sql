DROP TABLE IF EXISTS events CASCADE ;
DROP TABLE IF EXISTS categories CASCADE ;
DROP TABLE IF EXISTS users CASCADE ;
DROP TABLE IF EXISTS compilations CASCADE ;
DROP TABLE IF EXISTS compilation_events CASCADE ;
DROP TABLE IF EXISTS participation_requests CASCADE ;

CREATE TABLE users
(
    user_id  BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1) NOT NULL,
    name     VARCHAR(250)                                        NOT NULL,
    email    VARCHAR(254)                                        NOT NULL,
    CONSTRAINT pk_users_users PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE categories
(
    category_id     BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1) NOT NULL,
    category_name   VARCHAR(128)                                       NOT NULL,
    CONSTRAINT pk_categories_category PRIMARY KEY (category_id),
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (category_name)
);

CREATE TABLE events
(
    event_id        BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1)  NOT NULL,
    category_id     BIGINT                                              NOT NULL,
    annotation      VARCHAR(2000)                                       NOT NULL,
    confirmed_requests INTEGER                                          NOT NULL,
    created_on      TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    description     VARCHAR(7000)                                       NOT NULL,
    event_date      TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    user_id         BIGINT                                              NOT NULL,
    location_lat    FLOAT                                               NOT NULL,
    location_lon    FLOAT                                               NOT NULL,
    paid            BOOLEAN                                             NOT NULL,
    participant_limit INTEGER                                           NOT NULL,
    published_on    TIMESTAMP WITHOUT TIME ZONE                         NOT NULL,
    request_moderation BOOLEAN                                          NOT NULL,
    state           VARCHAR(20)                                         NOT NULL,
    title           VARCHAR(120)                                        NOT NULL,
    views           INTEGER                                             NOT NULL,
    CONSTRAINT pk_events_event PRIMARY KEY (event_id),
    CONSTRAINT events_categories_fk FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    CONSTRAINT events_users_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

CREATE TABLE compilations
(
    compilation_id  BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1) NOT NULL,
    title           VARCHAR(128)                                       NOT NULL,
    pinned          BOOLEAN                                            ,
    CONSTRAINT pk_compilations_compilation PRIMARY KEY (compilation_id),
    CONSTRAINT UQ_COMPILATION_TITLE UNIQUE (title)
);

CREATE TABLE compilation_events
(
    compilation_id    BIGINT                                             NOT NULL,
    event_id         BIGINT                                              NOT NULL,
    CONSTRAINT compilation_events_events_fk FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT compilation_events_compilations_fk FOREIGN KEY (compilation_id) REFERENCES compilations(compilation_id) ON DELETE CASCADE
);

CREATE TABLE participation_requests
(
    participation_id    BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1)  NOT NULL,
    created         TIMESTAMP WITHOUT TIME ZONE                             NOT NULL,
    event_id        BIGINT                                                  NOT NULL,
    user_id         BIGINT                                                  NOT NULL,
    status          VARCHAR(32)                                             NOT NULL,
    CONSTRAINT pk_participation_requests_participation PRIMARY KEY (participation_id),
    CONSTRAINT participation_requests_events_fk FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT participation_requests_users_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);