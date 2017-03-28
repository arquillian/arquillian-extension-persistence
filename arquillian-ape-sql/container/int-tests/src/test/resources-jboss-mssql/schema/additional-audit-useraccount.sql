IF OBJECT_ID('dbo.useraccount_audit', 'U') IS NOT NULL DROP TABLE dbo.useraccount_audit;
CREATE TABLE useraccount_audit (
  id        BIGINT       NOT NULL,
  firstname VARCHAR(128) NOT NULL,
  lastname  VARCHAR(128) NOT NULL,
  nickname  VARCHAR(128),
  password  VARCHAR(
            255)         NOT NULL,
  username  VARCHAR(32)  NOT NULL,
  openDate  DATE,
  PRIMARY KEY (id)
);
