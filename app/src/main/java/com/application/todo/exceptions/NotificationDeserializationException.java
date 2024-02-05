package com.application.todo.exceptions;

import com.application.todo.domain.notification.NotificationTopic;

public class NotificationDeserializationException extends RuntimeException {

    public NotificationDeserializationException(NotificationTopic topic, Throwable cause) {
        super(String.format("Cannot Deserialize the notification for topic [%s]", topic), cause);
    }
    
}
