USE ape;
START TRANSACTION;

  DROP TABLE IF EXISTS REVINFO;
  DROP TABLE IF EXISTS useraccount_address;
  DROP TABLE IF EXISTS useraccount;
  DROP TABLE IF EXISTS address;
  DROP TABLE IF EXISTS hibernate_sequence;

  CREATE TABLE REVINFO (
    REV      INTEGER NOT NULL AUTO_INCREMENT,
    REVTSTMP BIGINT,
    PRIMARY KEY (REV)
  );

  CREATE TABLE address (
    id          BIGINT       NOT NULL,
    city        VARCHAR(255) NOT NULL,
    houseNumber INTEGER,
    streetName  VARCHAR(255) NOT NULL,
    version     BIGINT,
    zipCode     INTEGER      NOT NULL,
    PRIMARY KEY (id)
  );

  CREATE TABLE useraccount (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    firstname VARCHAR(128) NOT NULL,
    lastname  VARCHAR(128) NOT NULL,
    nickname  VARCHAR(128),
    password  VARCHAR(255) NOT NULL,
    username  VARCHAR(32)  NOT NULL,
    openDate  DATE,
    PRIMARY KEY (id)
  );

  CREATE TABLE useraccount_address (
    useraccount_id BIGINT NOT NULL,
    addresses_id   BIGINT NOT NULL,
    PRIMARY KEY (useraccount_id, addresses_id),
    UNIQUE (addresses_id)
  );

  ALTER TABLE useraccount_address
    ADD INDEX FK538F4B7EC498202 (useraccount_id),
    ADD CONSTRAINT FK538F4B7EC498202 FOREIGN KEY (useraccount_id) REFERENCES useraccount (id);

  ALTER TABLE useraccount_address
    ADD INDEX FK538F4B757E57A74 (addresses_id),
    ADD CONSTRAINT FK538F4B757E57A74 FOREIGN KEY (addresses_id) REFERENCES address (id);

  CREATE TABLE hibernate_sequence (
    next_val BIGINT
  );

  INSERT INTO hibernate_sequence (next_val) VALUES (1);

COMMIT;
