package com.alok.iot.telemetry.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "iot")
public class IotProperties {
    private Mqtt mqtt;

    @Setter
    @Getter
    public static class Mqtt {
        private Broker broker;
        private Map<String, Device> devices;

        @Setter
        @Getter
        public static class Broker {
            private String host;
            private int port;
            private boolean autoReconnect;
            private boolean cleanState;
            private String clientId;
            private int connectionRetry;
            private int connectionTimeout;
            private int keepAlive;
            private int publishQos;
            private int subscribeQos;
            private Security security;

            @Setter
            @Getter
            public static class Security {
                private String caCertPath;
                private String clientKeystoreType;
                private String clientKeystorePath;
                private String clientKeystorePassword;

            }
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
            private String type;
            private String topic;

        }
    }
}