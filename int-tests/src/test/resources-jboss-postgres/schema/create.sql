DROP SEQUENCE IF EXISTS hibernate_sequence;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS useraccount CASCADE;
DROP TABLE IF EXISTS useraccount_address CASCADE;

CREATE TABLE address (
  id          INT8         NOT NULL,
  city        VARCHAR(255) NOT NULL,
  houseNumber INT4,
  streetName  VARCHAR(255) NOT NULL,
  version     INT8,
  zipCode     INT4         NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE useraccount (
  id        BIGSERIAL    NOT NULL,
  firstname VARCHAR(128) NOT NULL,
  lastname  VARCHAR(128) NOT NULL,
  nickname  VARCHAR(128),
  password  VARCHAR(255) NOT NULL,
  username  VARCHAR(32)  NOT NULL,
  openDate  DATE,
  PRIMARY KEY (id)
);

CREATE TABLE useraccount_address (
  useraccount_id INT8 NOT NULL,
  addresses_id   INT8 NOT NULL,
  PRIMARY KEY (useraccount_id, addresses_id),
  UNIQUE (addresses_id)
);

ALTER TABLE useraccount_address
  ADD CONSTRAINT FK538F4B7EC498202 FOREIGN KEY (useraccount_id) REFERENCES useraccount;

ALTER TABLE useraccount_address
  ADD CONSTRAINT FK538F4B757E57A74 FOREIGN KEY (addresses_id) REFERENCES address;

CREATE SEQUENCE hibernate_sequence START 1 INCREMENT 1;

