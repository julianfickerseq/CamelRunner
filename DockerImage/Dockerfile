FROM java:openjdk-8u72-jdk
MAINTAINER snuids

RUN apt-get update

RUN apt-get install zip -y
RUN apt-get install curl -y


RUN apt-get install maven -y
#RUN mvn -version


RUN curl -LOk https://github.com/snuids/CamelRunner/archive/master.zip
RUN unzip master.zip
#RUN ls -l
RUN rm master.zip

WORKDIR ./CamelRunner-master

RUN update-ca-certificates -f

RUN mvn install
RUN cp ./other/log4j.properties ./target/log4j.properties

RUN mkdir ./ImportXLS
RUN mkdir ./ImportXLSAndDelete


WORKDIR ./target

VOLUME ["/CamelRunner-master/configs"]
VOLUME ["/CamelRunner-master/ImportXLSAndDelete"]
VOLUME ["/CamelRunner-master/ImportXLS"]

EXPOSE 8778

ENV CONFIGFILE ../configs/config2.xml

ENTRYPOINT java -Dlog4j.configuration=file:./log4j.properties -jar CamelRunner.jar $CONFIGFILE
