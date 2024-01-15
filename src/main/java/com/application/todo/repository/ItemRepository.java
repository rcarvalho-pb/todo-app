package com.application.todo.repository;

import com.application.todo.domain.item.Item;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
}
