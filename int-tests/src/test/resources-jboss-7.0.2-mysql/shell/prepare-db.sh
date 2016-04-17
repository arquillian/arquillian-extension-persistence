#!/bin/sh
mysql -u root --password=letmein -e "CREATE DATABASE ape; GRANT ALL PRIVILEGES ON ape.* To 'ape'@'%' IDENTIFIED BY 'letmein';"
