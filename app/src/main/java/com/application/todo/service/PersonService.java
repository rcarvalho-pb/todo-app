package com.application.todo.service;

import com.application.todo.domain.person.Person;
import com.application.todo.exceptions.PersonNotFoundException;
import com.application.todo.repository.PersonRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class PersonService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.by("first_name"));
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Flux<Person> findAll() {
        return Flux.defer(() -> this.personRepository.findAll(DEFAULT_SORT))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Person> findById(final Long id) {
        return Mono.defer(() -> this.personRepository.findById(id)
                        .switchIfEmpty(Mono.error(new PersonNotFoundException(id))))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
