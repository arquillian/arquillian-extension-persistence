BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE useraccount_audit';
EXCEPTION
   WHEN OTHERS THEN NULL;
END;
/
create table useraccount_audit (id number(19,0) not null, firstname varchar2(128 char) not null,
                                lastname varchar2(128 char) not null, nickname varchar2(128 char),
                                password varchar2(255 char) not null, username varchar2(32 char) not null,
                                openDate date, primary key (id));