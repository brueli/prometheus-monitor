### IMAGE BUILD
FROM alpine:latest as build

# install JDK and maven
RUN apk add openjdk11
RUN apk add maven

# copy exporter files for compilation
WORKDIR /app
COPY monitoring-probe-core/ /app/monitoring-probe-core/
COPY monitoring-probe-sample/ /app/monitoring-probe-sample/
COPY monitoring-probe-application/ /app/monitoring-probe-application/

WORKDIR /app/monitoring-probe-core
RUN ./mvnw install

WORKDIR /app/monitoring-probe-sample
RUN ./mvnw install

WORKDIR /app/monitoring-probe-application
RUN ./mvnw package

# define entrypoint.sh as container entrypoint
#ENTRYPOINT ["/bin/sh"]


FROM alpine:latest as publish

RUN apk add openjdk11

WORKDIR /opt/monitoring-probe
COPY --from=build /app/monitoring-probe-application/target/*.jar ./

COPY bin/ ./bin/

#ENTRYPOINT ["/bin/sh"]
ENTRYPOINT ["/bin/sh", "-c", "./bin/entrypoint.sh"]
