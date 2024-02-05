package com.application.todo.domain.event;

import java.util.UUID;

import lombok.Data;

@Data
public class HeartBeat implements Event {
    private String id = UUID.randomUUID().toString();
}
