package com.application.todo.domain.event;

import com.application.todo.domain.item.Item;
import com.application.todo.domain.item.dto.ItemDTO;

import lombok.Value;

@Value
public class ItemSaved implements Event {

    ItemDTO itemDTO;    

    public ItemSaved(Item item) {
        this.itemDTO = Item.toDTO(item);
    }
}
