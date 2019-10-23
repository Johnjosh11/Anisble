FROM openjdk:8-jdk-alpine
USER root

RUN   apk update && apk add ca-certificates curl &&  update-ca-certificates
RUN adduser --home /home/user --disabled-password --gecos '' user
WORKDIR /home/user
ARG JAR_FILE
USER user

RUN curl -u selfservice:selfs3rvice -s -f -L https://artifactory.saunalahti.fi/selfservice-snapshot-local/product-api/product-category-server/trunk-SNAPSHOT/product-category-server-trunk-SNAPSHOT.jar -o ./app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]
