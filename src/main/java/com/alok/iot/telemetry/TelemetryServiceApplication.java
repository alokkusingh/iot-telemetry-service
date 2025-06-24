package com.alok.iot.telemetry;

import com.alok.iot.telemetry.properties.IotProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationPropertiesScan({
		"com.alok.iot.telemetry.properties",
		"com.alok.home.commons.security.properties"
})
@SpringBootApplication(scanBasePackages = {
		"com.alok.iot.telemetry",
		"com.alok.iot.telemetry.config",
		"com.alok.home.commons.exception",
		"com.alok.home.commons.security",
		"com.alok.home.commons.entity",
		"com.alok.home.commons.repository",
		"com.alok.home.commons.utils",
		"com.alok.home.commons.utils.annotations",
		"com.alok.home.commons.dto.api.response"
})
public class TelemetryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelemetryServiceApplication.class, args);
	}

}
