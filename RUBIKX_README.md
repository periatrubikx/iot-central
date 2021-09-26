#Setting up locally

Pre-requisite
``The application works on Java 11 only!!!``

1. Build the jar file by running the following command
   ``mvn clean install -DskipTests=true``
   alternatively, you can run the following too if you get some licence check errors
   ``mvn clean install -DskipTests=true -Dlicense.skip=true``

2. Start PG database server locally
   ``docker run -d --name tb-pg -e POSTGRES_PASSWORD=postgres -e PGDATA=/var/lib/postgresql/data/pgdata -v ${CUSTOM_MOUNT}:/var/lib/postgresql/data -p 6432:5432 postgres``

3. Create database called "thingsboard"

4. Setup base tables
   ``cd ${TB_WORK_DIR}/application/target/bin/install
   chmod +x install_dev_db.sh
   ./install_dev_db.sh``
   ``sudo ./install_dev_db.sh``

5. Bring up the Thingsboard Application by running the following command:
   ``java -jar application/target/thingsboard-${VERSION}-boot.jar
   ``

6. Access by visiting http://localhost:8080


# Hot Deploy for testing UI changes
``cd ui-ngx``
``mvn clean install -P yarn-start``


# Building Image for Deployment


# Deploying in Production


# WebSocket support (nginx 1.4)
When deploying the application behind Nginx, some services are failing. Add this to the nginx.conf file.


        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

#Entities and their significance
https://thingsboard.io/docs/user-guide/entities-and-relations/