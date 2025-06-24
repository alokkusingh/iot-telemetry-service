Home Telemetry Service

Home Stack Telemetry Service, which is a microservice that collects data from various sensors by listening MQTT Topics and processes it and transmits command to a sensor by publishing to a MQTT Topic.
## MQTT Topics
### Subscription Topics
- `home/alok/status/<<deviceId>>`
- `home/alok/telemetry/temperature/<<deviceId>>`
- `home/alok/telemetry/humidity/<<deviceId>>`
## MQTT Payloads
### Temperature Payload
```json
{
  "deviceId": "esp32-general-purpose-1",
  "temperature": 25.5,
  "unit": "Celsius"
}
```
### Humidity Payload
```json
{
  "deviceId": "esp32-general-purpose-1",
  "humidity": 60,
  "unit": "%"
}
```
### Device Status Payload
#### Online Status
```json
{
  "deviceId": "esp32-general-purpose-1",
  "status": "online",
  "lastSeen": "2023-10-01T12:00:00Z",
  "ipAddress": "192.168.1.6"
}
```
#### Offline Status
```json
{
  "deviceId": "esp32-general-purpose-1",
  "status": "offline",
  "lastSeen": "2023-10-01T12:00:00Z"
}
```
### Command Payload
```json
{
  "deviceId": "esp32-general-purpose-1",
  "command": "turn_on_fan"
}
```
### Publish Topics
- `home/alok/command/<<deviceId>>`

1. Maven Package
   ```shell
   mvn clean package -DskipTests
   ```
2. Docker Build, Push & Run
   ```shell
   docker build -t alokkusingh/iot-telemetry-service:latest -t alokkusingh/iot-telemetry-service:1.0.0 --build-arg JAR_FILE=target/iot-telemetry-service-1.0.0.jar .
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