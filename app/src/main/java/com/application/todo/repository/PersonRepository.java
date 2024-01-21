package com.application.todo.repository;

import com.application.todo.domain.person.Person;

import reactor.core.publisher.Mono;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends R2dbcRepository<Person, Long> {

    @Query("select p.* from person p join item_person ip on p.id = ip.person_id where ip.item_id = :item_id order by p.first_name")
    Mono<Person> findPersonByItemId(Long id);
}
