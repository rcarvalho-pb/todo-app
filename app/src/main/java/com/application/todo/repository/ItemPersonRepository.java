package com.application.todo.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.application.todo.domain.itemperson.ItemPerson;

import reactor.core.publisher.Mono;

@Repository
public interface ItemPersonRepository extends R2dbcRepository<ItemPerson, Long> {

    Mono<ItemPerson> findByItemId(Long itemId);

    Mono<Integer> deleteByItemId(Long itemId);
}
