DROP TABLE IF EXISTS meta_statistic;

CREATE TABLE IF NOT EXISTS meta_statistic
(
    id  BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 1) NOT NULL,
    app_name   VARCHAR(32)                                        NOT NULL,
    uri        VARCHAR(64)                                        NOT NULL,
    ip         VARCHAR(15)                                        NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE                        NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);