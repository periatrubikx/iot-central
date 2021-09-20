#!/bin/bash

docker rmi -f rubikx/iot-central:3.3.2-SNAPSHOT

cd ..

source ~/.profile

java11

mvn clean install -DskipTests=true -Dlicense.skip=true

docker build -t rubikx/iot-central:3.3.2-SNAPSHOT .
