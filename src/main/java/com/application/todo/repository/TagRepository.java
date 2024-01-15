package com.application.todo.repository;

import com.application.todo.domain.tag.Tag;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends R2dbcRepository<Tag, Long> {

    @Query("select t.* from tag t join item_tag it on t.id = it.tag_id where it.item_id = :item_id order by t.name")
    Flux<Tag> findTagsByItemId(Long itemId);
}
