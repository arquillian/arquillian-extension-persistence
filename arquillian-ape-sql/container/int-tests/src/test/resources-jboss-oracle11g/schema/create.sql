BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE useraccount_address CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE address CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/
BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE useraccount CASCADE CONSTRAINTS';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/
BEGIN
   EXECUTE IMMEDIATE 'DROP SEQUENCE hibernate_sequence';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/
create table address (id number(19,0) not null, city varchar2(255 char) not null, houseNumber number(10,0), streetName varchar2(255 char) not null, version number(19,0), zipCode number(10,0) not null, primary key (id));
create table useraccount (id number(19,0) not null, firstname varchar2(128 char) not null, lastname varchar2(128 char) not null, nickname varchar2(128 char), openDate date, password varchar2(255 char) not null, username varchar2(32 char) not null, primary key (id));
create table useraccount_address (useraccount_id number(19,0) not null, addresses_id number(19,0) not null, primary key (useraccount_id, addresses_id), unique (addresses_id));
alter table useraccount_address add constraint FK538F4B7EC498202 foreign key (useraccount_id) references useraccount;
alter table useraccount_address add constraint FK538F4B757E57A74 foreign key (addresses_id) references address;
create sequence hibernate_sequence start with 1 increment by 1;
