drop table if exists REVINFO
drop table if exists address
drop table if exists useraccount
drop table if exists useraccount_address
drop table if exists hibernate_sequence
create table REVINFO (REV integer not null auto_increment, REVTSTMP bigint, primary key (REV))
create table address (id bigint not null, city varchar(255) not null, houseNumber integer, streetName varchar(255) not null, version bigint, zipCode integer not null, primary key (id))
create table useraccount (id bigint not null auto_increment, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, username varchar(32) not null, openDate date, primary key (id))
create table useraccount_address (useraccount_id bigint not null, addresses_id bigint not null, primary key (useraccount_id, addresses_id), unique (addresses_id))
alter table useraccount_address add index FK538F4B7EC498202 (useraccount_id), add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount (id)
alter table useraccount_address add index FK538F4B757E57A74 (addresses_id), add constraint FK538F4B757E57A74 foreign key (addresses_id) references address (id)
create table hibernate_sequence ( next_val bigint )
insert into hibernate_sequence ( next_val ) values ( 1 )
