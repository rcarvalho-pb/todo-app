package com.application.todo.exceptions;

public class UnexpectedItemVersionException extends NotFoundException {
    public UnexpectedItemVersionException(final Long version, final Long expectedVersion) {
        super(String.format("Expected version: [%d], but got version: [%d]", expectedVersion, version));
    }
}
