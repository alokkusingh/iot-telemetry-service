Home Telemetry Service

Home Stack Telemetry Service, which is a microservice that collects data from various sensors by listening MQTT Topics and processes it and transmits command to a sensor by publishing to a MQTT Topic.
## MQTT Topics
### Subscription Topics
- `home/alok/status/<<deviceId>>`
- `home/alok/telemetry/temperature/<<deviceId>>`
- `home/alok/telemetry/humidity/<<deviceId>>`
### Publish Topics
- `home/alok/command/<<deviceId>>`

## Build
### Set JAVA_HOME (in case mvn run through terminal)
```shell
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```
### Build Application image
   ```shell
   docker build --progress=plain -f Dockerfile.native -t alokkusingh/iot-telemetry-service:latest -t alokkusingh/iot-telemetry-service:1.0.0 .
   ```
   ```shell
   docker push alokkusingh/iot-telemetry-service:latest
   ```
   ```shell
   docker push alokkusingh/iot-telemetry-service:1.0.0
   ```
   ```shell
   docker run -p 8081:8081 --rm --name iot-telemetry-service alokkusingh/iot-telemetry-service
   ```
### Manual commands - go inside and run binary manually
```shell
docker run -it --entrypoint /bin/bash -p 8081:8081 --rm --name iot-telemetry-service alokkusingh/iot-telemetry-service
```