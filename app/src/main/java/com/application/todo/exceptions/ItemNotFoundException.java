package com.application.todo.exceptions;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(final Long id) {
        super(String.format("Item [%d] not found", id));
    }
}
