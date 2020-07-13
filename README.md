HEALTHMONITOR PROBE APPLICATION
===============================


How to build
------------

### Build monitoring-probe-core
```
cd probe/monitoring-probe-core
.\mvnw install
```

```
cd probe/monitoring-probe-sample
.\mvnw install
```

```
cd probe/monitoring-probe-application
.\mvnw package
```


### Build docker image
```
cd probe/
docker build -t monitoring-probe:test .
```

### Run docker image locally
```
docker run -it monitoring-probe:test
```
