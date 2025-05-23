-- !Ups

CREATE TABLE bed_types (
  id IDENTITY,
  name TEXT NOT NULL,
  description TEXT NOT NULL
);

-- !Downs

DROP TABLE bed_types;
