package com.application.todo.controller;

import com.application.todo.domain.event.Event;
import com.application.todo.domain.event.ItemDeleted;
import com.application.todo.domain.event.ItemSaved;
import com.application.todo.domain.item.Item;
import com.application.todo.domain.item.dto.ItemDTO;
import com.application.todo.service.ItemService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import java.time.Duration;

@RestController
@RequestMapping("items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Mono<ItemDTO> create(@RequestBody final ItemDTO itemDto) {
        return Mono.defer(() -> this.itemService.create(new Item(itemDto)).map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @PutMapping(value = "/{id}")
    public Mono<ItemDTO> update(@PathVariable("id") final Long id, @RequestHeader(value = HttpHeaders.IF_MATCH) final Long version, @RequestBody final ItemDTO itemDTO) {
        return Mono.defer(() -> this.itemService.update(id, version, new Item(itemDTO)).map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    // @PatchMapping(value = "/{id}")
    // public Mono<ItemDTO> patch(@PathVariable final Long id, @RequestHeader(value = HttpHeaders.IF_MATCH) final Long version, @RequestBody final ItemDTO itemDTO) {
    //     return Mono.defer(() -> this.itemService.findById(id, version, true)
    //                     .flatMap(this.itemService::update)
    //                     .map(Item::toDTO))
    //             .subscribeOn(Schedulers.parallel());
    // }

    @GetMapping(value = "/{id}")
    public Mono<ItemDTO> findById(@PathVariable final Long id) {
        return Mono.defer(() -> this.itemService.findById(id, null, true).map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<ItemDTO> findAll() {
        return Flux.defer(() -> this.itemService.findAll().map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @GetMapping(value = "events")
    public Flux<ServerSentEvent<Event>> listenToEvent() {
        final Flux<Event> itemSavedFlux = this.itemService.listenToSavedItem().map(ItemSaved::new);

        final Flux<Event> itemDeletedFlux = this.itemService.listenToDeletedItems().map(ItemDeleted::new);

        return Flux.merge(itemSavedFlux, itemDeletedFlux).map(e -> ServerSentEvent.<Event>builder()
        .retry(Duration.ofSeconds(4L))
        .event(e.getClass().getSimpleName())
        .data(e).build());
    }
}
