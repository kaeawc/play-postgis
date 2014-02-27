
# --- !Ups

CREATE TABLE widget (
  id        BIGSERIAL    NOT NULL PRIMARY KEY,
  name      VARCHAR(255) NOT NULL,
  created   TIMESTAMP    NOT NULL
);

# --- !Downs

DROP TABLE widget;