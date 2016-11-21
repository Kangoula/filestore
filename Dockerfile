FROM maven:3.3
MAINTAINER Guillaume DENIS

# update and install usefull stuff
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git

# install wildfly 10.1
RUN wget -q http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.zip &&\
    unzip wildfly-10.1.0.Final.zip &&\
    mv wildfly-10.1.0.Final /opt/wildfly 

# get filestore
RUN git clone https://bitbucket.org/jayblanc/filestore &&\
    cd filestore &&\
    mvn clean package -DskipTests &&\
    cp filestore-ear/target/filestore-ear.ear /opt/wildfly/standalone/deployments/

# add wildfly config
WORKDIR /opt/wildfly
ADD ./configuration.zip .
RUN unzip configuration.zip &&\
    cp -r configuration/* ./standalone/configuration/

# add wildfly startup script
WORKDIR /
ADD ./startWildfly.sh .

EXPOSE 8080

ENTRYPOINT ["./startWildfly.sh"]
