package com.application.todo.controller;

import com.application.todo.domain.tag.Tag;
import com.application.todo.domain.tag.dto.TagDTO;
import com.application.todo.service.TagService;
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
@RequestMapping("tags")
public class TagController {

    public final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON_VALUE})
    public Mono<TagDTO> findById(@PathVariable final Long id) {
        return Mono.defer(() -> this.tagService.findById(id).map(Tag::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<TagDTO> getAll() {
        return Flux.defer(() -> this.tagService.findAll().map(Tag::toDTO))
                .subscribeOn(Schedulers.parallel());
    }
}
