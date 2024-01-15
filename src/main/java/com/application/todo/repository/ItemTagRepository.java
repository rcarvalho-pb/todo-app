package com.application.todo.repository;


import com.application.todo.domain.itemtag.ItemTag;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemTagRepository extends R2dbcRepository<ItemTag, Long> {

    Flux<ItemTag> findAllByItemId(Long itemId);

    Mono<Integer> deleteAllByItemId(Long itemId);

}
