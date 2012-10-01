drop table if exists useraccount_audit cascade
create table useraccount_audit (id  bigserial not null, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, username varchar(32) not null, openDate date, primary key (id))

