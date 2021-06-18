#!/bin/bash

java -jar -Dspring.config.location=../config/application.properties ../lib/springboot-ftp-demo-0.0.1-SNAPSHOT.jar $1 > ../logs/ftp.log 2>&1 &

echo $! > tpids

echo Start Success!