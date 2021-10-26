BEGIN;

CREATE SCHEMA hello;

CREATE TABLE hello.users (
  user_id  UUID NOT NULL,
  name     TEXT NOT NULL,
  email    VARCHAR(256) NOT NULL UNIQUE,
  password TEXT NOT NULL,
  PRIMARY KEY (user_id)
);

COMMIT;