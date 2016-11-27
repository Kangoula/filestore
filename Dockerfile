FROM openjdk:8
MAINTAINER Guillaume DENIS

# update and install usefull stuff
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y \
    wget \
    unzip

# install wildfly 10.1
RUN wget -q http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.zip &&\
    unzip wildfly-10.1.0.Final.zip &&\
    mv wildfly-10.1.0.Final /opt/wildfly

# add fix in wildfly
ADD ./utils/module_wildfly/dom4j-1.6.1.jar /opt/wildfly/modules/system/layers/base/org/dom4j/main/dom4j-1.6.1.jar 

VOLUME /opt/wildfly/standalone/deployments/

# add wildfly config
WORKDIR /opt/wildfly

ADD ./utils/configuration.zip .
RUN unzip configuration.zip &&\
    cp -r configuration/* ./standalone/configuration/

# add wildfly startup script
WORKDIR /
ADD ./utils/startWildfly.sh .

EXPOSE 8080

ENTRYPOINT ["./startWildfly.sh"]
