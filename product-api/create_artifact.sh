#!/bin/bash

mvn package

curl -v -X PUT --user selfservice:selfs3rvice --upload-file './product-category-server/target/product-category-server-1.0-SNAPSHOT.jar' https://artifactory.saunalahti.fi/selfservice-release-local/product-api/product-category-server-1.0-SNAPSHOT.jar
