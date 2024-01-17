package com.application.todo.service;

import com.application.todo.domain.tag.Tag;
import com.application.todo.exceptions.TagNotFoundException;
import com.application.todo.repository.TagRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class TagService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.by("name"));

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Flux<Tag> findAll() {
        return Flux.defer(() -> this.tagRepository.findAll(DEFAULT_SORT))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Tag> findById(final Long id) {
        return Mono.defer(() -> this.tagRepository.findById(id)
                        .switchIfEmpty(Mono.error(new TagNotFoundException(id))))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
