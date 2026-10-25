ARG BASE_IMAGE
ARG QBE_VERSION=1.2
FROM ubuntu:24.04
LABEL maintainer="Patrick.Reignier@imag.fr"
RUN apt-get update -yq
RUN apt-get install -yq tzdata && ln -fs /usr/share/zoneinfo/Europe/Paris /etc/localtime && dpkg-reconfigure -f noninteractive tzdata
RUN apt-get install -yq wget 
RUN cd /usr/local; wget "https://github.com/adoptium/temurin23-binaries/releases/download/jdk-23.0.2+7/OpenJDK23U-jdk_x64_linux_hotspot_23.0.2_7.tar.gz"
RUN cd /usr/local; tar xzvf OpenJDK23U-jdk_x64_linux_hotspot_23.0.2_7.tar.gz
ENV JAVA_HOME=/usr/local/jdk-23.0.2+7
ENV PATH=$JAVA_HOME/bin:$PATH
ENV PATH=/usr/local/ima/bin:"$PATH"
ENV PATH=/home/gl/projet_gl/src/main/bin:"$PATH"
ENV PATH=/home/gl/projet_gl/src/test/script:"$PATH"
ENV PATH=/home/gl/projet_gl/src/test/script/launchers:"$PATH"
RUN apt-get -yq install sudo maven make gcc gnat-9 locales git time
RUN apt-get update -yq && apt-get install -yq xz-utils ca-certificates
RUN wget https://archive.apache.org/dist/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz -P /tmp
RUN tar xzvf /tmp/apache-maven-*.gz -C /opt
RUN ln -s /opt/apache-maven-3.8.4 /opt/maven
RUN apt-get clean
RUN mkdir -p /usr/local/qbe/src /usr/local/qbe/bin
COPY qbe-1.2.tar.xz /usr/local/qbe/src/
RUN cd /usr/local/qbe/src && tar -xf qbe-1.2.tar.xz
RUN cd /usr/local/qbe/src/qbe-1.2 && make
RUN install -m 0755 /usr/local/qbe/src/qbe-1.2/qbe /usr/local/qbe/bin/qbe
ENV PATH=/usr/local/qbe/bin:$PATH
RUN mkdir /usr/local/rars
COPY rars.jar /usr/local/rars/rars.jar
ENV RARS_JAR=/usr/local/rars/rars.jar
RUN mkdir /usr/local/ima
COPY ima_sources.tgz /usr/local/ima
RUN cd /usr/local/ima && tar xzvf ima_sources.tgz
RUN locale-gen --no-purge en_US.UTF-8
ENV LC_ALL=en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en 
RUN userdel ubuntu
RUN useradd -ms /bin/bash gl -u 1000 && usermod -aG sudo gl
RUN echo gl:gl | chpasswd
USER gl
COPY bashrc /home/gl/.bashrc
RUN mkdir /home/gl/projet_gl
VOLUME /home/gl/projet_gl
WORKDIR /home/gl/projet_gl
CMD ["/bin/bash"]