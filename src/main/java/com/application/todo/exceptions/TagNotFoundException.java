package com.application.todo.exceptions;

public class TagNotFoundException extends NotFoundException {
    public TagNotFoundException(final Long id) {
        super(String.format("Tag [%d] not found", id));
    }
}
