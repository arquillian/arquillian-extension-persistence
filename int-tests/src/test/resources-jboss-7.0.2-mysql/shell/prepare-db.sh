#!/bin/bash
mysql -h 127.0.0.1 -u root --password=letmein -P 33306 -e "CREATE DATABASE ape; GRANT ALL PRIVILEGES ON ape.* To 'ape'@'%' IDENTIFIED BY 'letmein';"
