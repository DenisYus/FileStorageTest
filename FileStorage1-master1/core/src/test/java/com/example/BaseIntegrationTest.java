package com.example;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.net.URI;
import java.net.URISyntaxException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {
    @LocalServerPort
    Integer port = 0;

    @Autowired
    protected TestRestTemplate restTemplate;
    public URI serverUrl(String path) throws URISyntaxException {
        return new URI("http://localhost:"+port).resolve(path);
    }

    @Container
    static PostgreSQLContainer postgresContainer = createContainer();

    static PostgreSQLContainer createContainer() {
        var container = new PostgreSQLContainer<>("postgres:latest");
        container.start();
        return container;
    }

    @DynamicPropertySource
    @SuppressWarnings("unused")
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
}
