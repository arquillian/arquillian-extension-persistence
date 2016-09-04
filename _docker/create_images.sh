#!/bin/bash

TAGS=(8.1.0.Final 8.2.0.Final 8.2.1.Final 9.0.0.Final 9.0.1.Final 9.0.2.Final 10.0.0.Final 10.1.0.Final)

for tag in ${TAGS[@]}; do
    sed -e "s/<VERSION>/${tag}/g" Dockerfile.tpl > Dockerfile
    docker build --tag=jboss/wildfly-admin:${tag} .
    rm Dockerfile
done
