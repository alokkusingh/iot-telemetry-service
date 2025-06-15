Home Telemetry Service

Home Stack Telemetry Service, which is a microservice that collects data from various sensors by listening MQTT Topics and processes it and transmits command to a sensor by publishing to a MQTT Topic.
## MQTT Topics
### Subscription Topics
- `home/alok/telemetry/status`
- `home/alok/telemetry/temperature`
- `home/alok/telemetry/humidity`
### Publish Topics
When device boots-up the status is published to the topic, which includes device command topic. The service will use this topic to publish the command to the device.