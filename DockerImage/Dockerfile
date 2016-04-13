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
RUN ls -l
RUN cat pom.xml
RUN ls -l
RUN cp ./other/log4j.properties ./target/log4j.properties

WORKDIR ./target
RUN java -Dlog4j.configuration=file:./log4j.properties -jar CamelRunner.jar ../configs/config2.xml
