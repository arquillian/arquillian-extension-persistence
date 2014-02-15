IF OBJECT_ID('dbo.useraccount_audit', 'U') IS NOT NULL DROP TABLE dbo.useraccount_audit;
create table useraccount_audit (id bigint not null, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(
255) not null, username varchar(32) not null, openDate date, primary key (id))