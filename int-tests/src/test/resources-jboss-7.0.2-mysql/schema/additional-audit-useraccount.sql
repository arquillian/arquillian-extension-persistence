drop table if exists useraccount_audit
create table useraccount_audit (id bigint not null auto_increment, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, username varchar(32) not null, openDate date, primary key (id))
