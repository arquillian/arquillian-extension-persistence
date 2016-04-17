#!/bin/sh
gosu postgres psql -c "CREATE DATABASE ape;"
gosu postgres psql -c "CREATE USER ape WITH PASSWORD 'letmein'; GRANT ALL PRIVILEGES ON DATABASE ape TO ape;"
