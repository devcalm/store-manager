package org.devcalm.store.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
