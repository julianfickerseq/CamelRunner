#!/bin/bash
while true
do
  echo 'Starting from shell'
  java -Dlog4j.configuration=file:./log4j.properties -jar CamelRunner.jar ./configs/config.xml
  sleep 1  
done

