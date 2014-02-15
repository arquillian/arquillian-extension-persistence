IF OBJECT_ID('dbo.useraccount_address', 'U') IS NOT NULL DROP TABLE dbo.useraccount_address;
IF OBJECT_ID('dbo.address', 'U') IS NOT NULL DROP TABLE dbo.address;
IF OBJECT_ID('dbo.useraccount', 'U') IS NOT NULL DROP TABLE dbo.useraccount;
IF OBJECT_ID('dbo.hibernate_sequence', 'U') IS NOT NULL DROP TABLE dbo.hibernate_sequence;

create table address (id bigint not null, city varchar(255) not null, houseNumber int, streetName varchar(255) not null, version bigint, zipCode int not null, primary key (id));
create table useraccount (id bigint not null, firstname varchar(128) not null, lastname varchar(128) not null, nickname varchar(128), password varchar(255) not null, username varchar(32) not null, openDate date, primary key (id));
create table useraccount_address (useraccount_id bigint not null, addresses_id bigint not null, primary key (useraccount_id, addresses_id), unique (addresses_id));
alter table useraccount_address add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount;
alter table useraccount_address add constraint FK538F4B757E57A74 foreign key (addresses_id) references address;
create table hibernate_sequence ( next_val bigint );
insert into hibernate_sequence values ( 1 );

