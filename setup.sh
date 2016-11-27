#!/bin/sh

EAR_PATH=/home/kangoula/code/filestore/ear/
S3_BUKET=tartempion
S3_KEY=tartempion
AWS_SECRET=tartempion

docker build -t kangoula/filestore:latest .

docker run -d -p 8080:8080 -v /opt/wildfly/standalone/deployments/filestore.ear:$EAR_PATH \
-e S3_BUCKET=$S3_BUCKET \
-e S3_KEY=$S3_KEY \
-e AWS_KEY=$AWS_SECRET \
kangoula/filestore:latest
