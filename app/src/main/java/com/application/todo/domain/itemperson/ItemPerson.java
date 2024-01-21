package com.application.todo.domain.itemperson;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@NoArgsConstructor
@Data
public class ItemPerson {
    
    @Id
    private Long id;

    @NotNull
    private Long itemId;

    @NotNull
    private Long personId;

    public ItemPerson(Long itemId, Long personId) {
        this.itemId = itemId;
        this.personId = personId;
    }
}
