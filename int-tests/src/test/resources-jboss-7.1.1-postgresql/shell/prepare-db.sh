#!/bin/bash
psql -h 127.0.0.1 -U postgres -p 65432 -c "CREATE DATABASE ape;"
psql -h 127.0.0.1 -U postgres -d ape -p 65432 -c "CREATE USER ape WITH PASSWORD 'letmein'; GRANT ALL PRIVILEGES ON DATABASE ape TO ape;"
