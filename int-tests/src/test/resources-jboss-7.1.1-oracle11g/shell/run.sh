#!/bin/bash
sshpass -p admin ssh -v -t -oStrictHostKeyChecking=no root@localhost -p 49160 < $1