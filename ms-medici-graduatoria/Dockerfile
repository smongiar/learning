FROM fabric8/java-alpine-openjdk8-jdk

MAINTAINER francesco.rotello@eng.it

ENV TZ=Europe/Rome

ENV AB_OFF true
ENV JAVA_APP_JAR java-container.jar
ENV JAVA_OPTIONS -Xmx256m

EXPOSE 9091

ADD target/*.jar /deployments/java-container.jar