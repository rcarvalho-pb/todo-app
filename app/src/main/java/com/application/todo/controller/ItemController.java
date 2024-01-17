package com.application.todo.controller;

import com.application.todo.domain.item.Item;
import com.application.todo.domain.item.dto.ItemDTO;
import com.application.todo.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Mono<ItemDTO> create(@RequestBody final ItemDTO itemDto) {
        Item item = new Item(itemDto);
        log.info("{}", item);
        return Mono.defer(() -> this.itemService.create(new Item(itemDto)).map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @PutMapping(value = "/{id}")
    public Mono<ItemDTO> update(@PathVariable final Long id, @RequestHeader(value = HttpHeaders.IF_MATCH) final Long version, @RequestBody final ItemDTO itemDTO) {
        return Mono.defer(() -> this.itemService.findById(id, version, false)
                        .flatMap(this.itemService::update)
                        .map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

    @PatchMapping(value = "/{id}")
    public Mono<ItemDTO> patch(@PathVariable final Long id, @RequestHeader(value = HttpHeaders.IF_MATCH) final Long version, @RequestBody final ItemDTO itemDTO) {
        return Mono.defer(() -> this.itemService.findById(id, version, true)
                        .flatMap(this.itemService::update)
                        .map(Item::toDTO))
                .subscribeOn(Schedulers.parallel());
    }

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
}
