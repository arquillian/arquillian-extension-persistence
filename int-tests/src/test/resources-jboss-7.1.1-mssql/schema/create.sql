IF OBJECT_ID('dbo.useraccount_address', 'U') IS NOT NULL
  DROP TABLE dbo.useraccount_address;;

IF OBJECT_ID('dbo.address', 'U') IS NOT NULL
  DROP TABLE dbo.address;;

IF OBJECT_ID('dbo.useraccount', 'U') IS NOT NULL
  DROP TABLE dbo.useraccount;;

IF OBJECT_ID('dbo.hibernate_sequence', 'U') IS NOT NULL
  DROP TABLE dbo.hibernate_sequence;;


CREATE TABLE address (
  id          BIGINT       NOT NULL,
  city        VARCHAR(255) NOT NULL,
  houseNumber INT,
  streetName  VARCHAR(255) NOT NULL,
  version     BIGINT,
  zipCode     INT          NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE useraccount (
  id        BIGINT       NOT NULL,
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
  ADD CONSTRAINT FK538F4B7EC498202 FOREIGN KEY (useraccount_id) REFERENCES useraccount;

ALTER TABLE useraccount_address
  ADD CONSTRAINT FK538F4B757E57A74 FOREIGN KEY (addresses_id) REFERENCES address;

CREATE TABLE hibernate_sequence (
  next_val BIGINT
);

INSERT INTO hibernate_sequence VALUES (1);


