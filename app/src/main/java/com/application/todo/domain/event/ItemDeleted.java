package com.application.todo.domain.event;


import lombok.Value;

@Value
public class ItemDeleted implements Event{
    
    Long itemId;

    public ItemDeleted(Long id) {
        this.itemId = id;
    }
}
