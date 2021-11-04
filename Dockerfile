FROM openjdk:8-jdk-alpine as build

RUN wget -q https://services.gradle.org/distributions/gradle-7.2-bin.zip \
    && unzip gradle-7.2-bin.zip -d /opt \
    && rm gradle-7.2-bin.zip
ENV GRADLE_HOME /opt/gradle-7.2
ENV PATH $PATH:/opt/gradle-7.2/bin

RUN mkdir $HOME/build
WORKDIR $HOME/build

COPY *.gradle gradle.* settings.gradle.kts $HOME/build/
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

COPY . $HOME/build/
RUN gradle clean build --no-daemon -x test

FROM openjdk:8-jdk-alpine as app
COPY --from=build $HOME/build/app/build/distributions/*.tar $HOME/app/
WORKDIR $HOME/app/
RUN tar -xvf app-1.0.tar
WORKDIR $HOME/app/app-1.0
EXPOSE 8080
CMD bin/app
