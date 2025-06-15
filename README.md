Home Telemetry Service

Home Stack Telemetry Service, which is a microservice that collects data from various sensors by listening MQTT Topics and processes it and transmits command to a sensor by publishing to a MQTT Topic.
## MQTT Topics
### Subscription Topics
- `home/alok/status/<<deviceId>>`
- `home/alok/telemetry/temperature/<<deviceId>>`
- `home/alok/telemetry/humidity/<<deviceId>>`
### Publish Topics
- `home/alok/command/<<deviceId>>`