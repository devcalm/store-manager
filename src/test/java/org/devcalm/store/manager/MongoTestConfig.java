package org.devcalm.store.manager;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.testcontainers.containers.MongoDBContainer;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "org.devcalm.store.manager.domain.repository")
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class MongoTestConfig {

    @Bean
    public MongoDBContainer mongoDBContainer() {
        var mongoDBContainer = new MongoDBContainer("mongo:8");
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
        return mongoDBContainer;
    }
}
