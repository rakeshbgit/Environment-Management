package com.apimanagement.Environment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class EnvironmentManageApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnvironmentManageApplication.class, args);
	}

}
