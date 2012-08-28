drop sequence if exists hibernate_sequence
drop table if exists address cascade
drop table if exists useraccount cascade
drop table if exists useraccount_address cascade
create table address (id int8 not null, city varchar(255) not null, houseNumber int4, streetName varchar(255) not null, version int8, zipCode int4 not null, primary key (id))
create table useraccount (id  bigserial not null, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, username varchar(32) not null, primary key (id))
create table useraccount_address (useraccount_id int8 not null, addresses_id int8 not null, primary key (useraccount_id, addresses_id), unique (addresses_id))
alter table useraccount_address add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount
alter table useraccount_address add constraint FK538F4B757E57A74 foreign key (addresses_id) references address
create sequence hibernate_sequence start 1 increment 1
