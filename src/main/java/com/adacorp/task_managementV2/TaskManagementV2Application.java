package com.adacorp.task_managementV2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaskManagementV2Application {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementV2Application.class, args);
	}

}
