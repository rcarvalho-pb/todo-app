package com.application.todo.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.application.todo.domain.notification.NotificationTopic;
import com.application.todo.exceptions.NotificationDeserializationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.micrometer.common.util.StringUtils;
import io.r2dbc.postgresql.api.PostgresqlConnection;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class NotificationService {
    
    private final ConnectionFactory connectionFactory;
    private final Set<NotificationTopic> watchedTopics = new HashSet<>();
    private PostgresqlConnection postgresqlConnection;
    private ObjectMapper objectMapper;

    public NotificationService(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public <T> Flux<T> listen(final NotificationTopic topic, final Class<T> clazz) {
        if (!this.watchedTopics.contains(topic)) {
            synchronized (this.watchedTopics) {
                if (!this.watchedTopics.contains(topic)) {
                    this.executeListenStatment(topic);
                    this.watchedTopics.add(topic);
                }
            }
        }

        return this.getConnection().getNotifications()
            .filter(notification -> topic.name().equals(notification.getName()) && notification.getParameter() != null)
            .handle((notification, sink) -> {
                final String json = notification.getParameter();
                if (!StringUtils.isBlank(json)) {
                    try {
                        sink.next(objectMapper.readValue(json, clazz));
                    } catch (JsonProcessingException e) {
                        log.error(String.format("Problem deserializing the json: %s", json), e);
                        Mono.error(new NotificationDeserializationException(topic, e));
                    }
                }
            });
    }

    public void unlisten(final NotificationTopic topic) {
        if (this.watchedTopics.contains(topic)) {
            synchronized (this.watchedTopics) {
                if (this.watchedTopics.contains(topic)) {
                    this.executeUnlistenStatment(topic);
                    this.watchedTopics.remove(topic);
                }
            }
        }
    }

    @PostConstruct
    private void postConstruct() {
        this.objectMapper = this.createObjectMapper();
    }

    @PreDestroy
    private void preDestroy() {
        this.getConnection().close().subscribe();
    }

    private void executeListenStatment(final NotificationTopic topic) {
        log.info("Connection - {}", getConnection().toString());
        this.getConnection().createStatement(String.format("LISTEN \"%s\"", topic)).execute().subscribe();
    }

    private void executeUnlistenStatment(final NotificationTopic topic) {
        this.getConnection().createStatement(String.format("UNLISTEN \"%s\"", topic)).execute().subscribe();
    }

    private PostgresqlConnection getConnection() {
        if (this.postgresqlConnection == null) {
            synchronized (NotificationService.class) {
                Mono.from(this.connectionFactory.create())
                    .cast(PostgresqlConnection.class).subscribe(con -> {
                        this.postgresqlConnection = con;
                        log.info("PostgresqlConnection - {}", con);
                    });
            }
        }

        log.info("Postgres", this.postgresqlConnection);

        return this.postgresqlConnection;
    }

    private ObjectMapper createObjectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
