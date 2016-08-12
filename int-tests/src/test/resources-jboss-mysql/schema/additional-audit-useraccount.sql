USE ape;
DROP TABLE IF EXISTS useraccount_audit;

CREATE TABLE useraccount_audit (
  id        BIGINT       NOT NULL AUTO_INCREMENT,
  firstname VARCHAR(128) NOT NULL,
  lastname  VARCHAR(128) NOT NULL,
  nickname  VARCHAR(128),
  password  VARCHAR(255) NOT NULL,
  username  VARCHAR(32)  NOT NULL,
  openDate  DATE,
  PRIMARY KEY (id)
);

