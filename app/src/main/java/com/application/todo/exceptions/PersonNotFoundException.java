package com.application.todo.exceptions;

public class PersonNotFoundException extends NotFoundException {
    public PersonNotFoundException(final Long id) {
        super(String.format("Person [%d] not found", id));
    }
}
