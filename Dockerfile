FROM java:openjdk-8u72-jdk
MAINTAINER snuids

VOLUME ["/configs"]
VOLUME ["/ImportXLSAndDelete"]
VOLUME ["/ImportXLS"]

EXPOSE 8778

#ENV CONFIGFILE ./configs/config.xml

COPY ./target/CamelRunner.jar ./CamelRunner.jar
COPY ./target/lib ./lib
COPY ./other/log4j.properties ./log4j.properties
COPY ./Shells/start.sh ./start.sh

RUN chmod ugo+x ./start.sh

RUN ls -l
RUN pwd

ENTRYPOINT ./start.sh
