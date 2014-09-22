#!/bin/sh

export ORACLE_HOME=/u01/app/oracle/product/11.2.0/xe
export PATH=$ORACLE_HOME/bin:$PATH
export ORACLE_SID=XE

rm -f ape.sql

cat<<EOT >> ape.sql
CREATE USER ape IDENTIFIED BY letmein;

GRANT create session TO ape;
GRANT create table TO ape;
GRANT create view TO ape;
GRANT create any trigger TO ape;
GRANT create any procedure TO ape;
GRANT create sequence TO ape;
GRANT create synonym TO ape;

alter database default tablespace users;
alter user ape quota 50m on users;
alter user ape quota 50m on system;
exit;
EOT

exec /u01/app/oracle/product/11.2.0/xe/bin/sqlplus system/oracle @ape.sql
