package com.alok.iot.telemetry.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "iot")
@Getter
@Setter
public class IotProperties {
    private Mqtt mqtt;

    @Setter
    @Getter
    public static class Mqtt {
        private Broker broker;
        private Map<String, Device> devices;

        @Getter
        @Setter
        public static class Broker {
            private String host;
            private int port;
            private boolean autoReconnect;
            private boolean cleanState;
            private int connectionRetry;
            private int connectionTimeout;
            private int keepAlive;
            private int publishQos;
            private int subscribeQos;
        }

        @Setter
        @Getter
        public static class Device {
            private String name;
            private String type;
            private String location;
            private String statusTopic;
            private String commandTopic;
            private Map<String, Sensor> sensors;
        }

        @Setter
        @Getter
        public static class Sensor {
            // getters and setters
            private String type;
            private String topic;
        }
    }
}