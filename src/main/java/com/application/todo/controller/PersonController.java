package com.application.todo.controller;

import com.application.todo.domain.person.Person;
import com.application.todo.domain.person.dto.PersonDTO;
import com.application.todo.service.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Mono<PersonDTO> findById(@PathVariable final Long id) {
        return Mono.defer(() -> this.personService.findById(id).map(Person::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<PersonDTO> getAll() {
        return Flux.defer(() -> this.personService.findAll().map(Person::toDTO))
                .subscribeOn(Schedulers.parallel());
    }
}
