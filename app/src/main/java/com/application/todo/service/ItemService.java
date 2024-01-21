package com.application.todo.service;

import java.util.List;

import com.application.todo.domain.item.Item;
import com.application.todo.domain.itemperson.ItemPerson;
import com.application.todo.domain.itemtag.ItemTag;
import com.application.todo.domain.tag.Tag;
import com.application.todo.exceptions.ItemNotFoundException;
import com.application.todo.exceptions.UnexpectedItemVersionException;
import com.application.todo.repository.ItemPersonRepository;
import com.application.todo.repository.ItemRepository;
import com.application.todo.repository.ItemTagRepository;
import com.application.todo.repository.PersonRepository;
import com.application.todo.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.Collection;


@Slf4j
@Service
public class ItemService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Order.by("lastModifiedDate"));
    private final ItemRepository itemRepository;
    private final PersonRepository personRepository;
    private final ItemTagRepository itemTagRepository;
    private final TagRepository tagRepository;
    private final ItemPersonRepository itemPersonRepository;

    public ItemService(ItemRepository itemRepository, PersonRepository personRepository, ItemTagRepository itemTagRepository, TagRepository tagRepository, ItemPersonRepository itemPersonRepository) {
        this.itemRepository = itemRepository;
        this.personRepository = personRepository;
        this.itemTagRepository = itemTagRepository;
        this.tagRepository = tagRepository;
        this.itemPersonRepository = itemPersonRepository;
    }

    public Flux<Item> findAll() {
        return Flux.defer(() -> this.itemRepository.findAll(DEFAULT_SORT)
            .flatMap(item -> this.loadRelations(item)))
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<Item> create(Item item) {
        log.info("Create item in ItemService: Item - {}", item);
        if (item.getId() != null || item.getVersion() != null) {
            return Mono.error(new IllegalArgumentException("when creating an item, the id and the version must be null"));
        }

        return Mono.defer(() -> this.itemRepository
            .save(item)
            .flatMap(si -> {
                if (si.getAssigneeId() != null) return this.itemPersonRepository.save(new ItemPerson(si.getId(), si.getAssigneeId()))
                        .then(Mono.just(si));
                return Mono.just(si);
            }).flatMap(si -> {
                if (si.getTags() != null) return this.tagRepository.saveAll(si.getTags())
                        .flatMap(tag -> this.itemTagRepository.save(new ItemTag(si.getId(), tag.getId())))
                        .then(Mono.just(si));
                return Mono.just(si);
            })
            .then(Mono.just(item).flatMap(this::loadRelations)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<Item> update(final Long id, final Long version, final Item item) {
        log.info("{}", item);
        if (item.getId() == null || item.getVersion() == null) {
            return Mono.error(new IllegalArgumentException("When updating an item, id and version must not be null"));
        }

        return Mono.defer(() -> this.verifyExistence(id)
            .then(this.itemPersonRepository.findByItemId(id))
            .flatMap(i -> {
                if(i != null) return this.itemPersonRepository.delete(i);
                return Mono.empty();
            })
            .then(Mono.empty()))
            .flatMap(i -> {
                if(item.getAssigneeId() != null) return this.itemPersonRepository.save(new ItemPerson(item.getId(), item.getAssigneeId()));
                return Mono.empty();
            })
            .then(this.itemTagRepository.findAllByItemId(id).collectList())
            .flatMap(itemTags -> {
                final Collection<Long> existingTagsId = itemTags.stream()
                            .map(ItemTag::getTagId)
                            .toList();
                final Collection<Long> tagIdsToSave = item.getTags().stream()
                        .map(Tag::getId)
                        .toList();

                final Collection<ItemTag> removedItemTags = itemTags.stream()
                        .filter(itemTag -> !tagIdsToSave.contains(itemTag.getTagId()))
                        .toList();

                final Collection<ItemTag> addedItemTags = tagIdsToSave.stream()
                        .filter(tagid -> !existingTagsId.contains(tagid))
                        .map(tagId -> new ItemTag(item.getId(), tagId))
                        .toList();
                
                log.info("existingTagsId - {}", existingTagsId);
                log.info("tagIdsToSave - {}", tagIdsToSave);
                log.info("removedItemTags - {}", removedItemTags);
                log.info("addedItemTags - {}", addedItemTags);

                return this.itemTagRepository.deleteAll(removedItemTags)
                .then(this.itemTagRepository.saveAll(addedItemTags).collectList());
            })
            .then(this.tagRepository.findAllById(item.getTags().stream().map(Tag::getId).toList()).collectList())
            .flatMap(tags -> {
                log.info("Tags - {}", tags);
                return this.tagRepository.saveAll(item.getTags()).collectList();
            })
            .then(this.itemRepository.findById(id))
            .flatMap(i -> {
                if(item.getStatus() != null) i.setStatus(item.getStatus());
                if(item.getDescription() != null) i.setDescription(item.getDescription());
                if(i.getAssigneeId() != null && i.getAssigneeId() != item.getAssigneeId()) i.setAssigneeId(item.getAssigneeId());

                return this.itemRepository.save(i).flatMap(this::loadRelations);
            })
        .subscribeOn(Schedulers.boundedElastic());
    }

    @Transactional
    public Mono<Void> deleteById(final Long id, final Long version) {
        return Mono.defer(() -> this.findById(id, version, false)
                    .zipWith(this.itemTagRepository.deleteAllByItemId(id))
                    .map(Tuple2::getT1)
                    .flatMap(this.itemRepository::delete)
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Item> findById(final Long id, final Long version, final boolean loadRelations) {
        final Mono<Item> itemMono = this.itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ItemNotFoundException(id)))
                .handle((item, sink) -> {
                    if (version != null && !version.equals(item.getVersion())) sink.error(new UnexpectedItemVersionException(version, item.getVersion()));
                    else sink.next(item);
                });

        return loadRelations ? itemMono.flatMap(this::loadRelations) : itemMono;
    }

    private Mono<Item> loadRelations(final Item item) {
        Flux<ItemTag> itemTagMono = this.itemTagRepository.findAllByItemId(item.getId());
        Mono<Item> mono = Mono.just(item);
        if (itemTagMono != null) mono = mono
                .zipWith(tagRepository.findTagsByItemId(item.getId()).collectList())
                .map(res -> res.getT1().setTags(res.getT2()));

        if (item.getAssigneeId() != null) mono = mono
                .zipWith(this.personRepository.findById(item.getAssigneeId()))
                .map(res -> res.getT1().setAssignee(res.getT2()));

        return mono;
    }

    private Mono<Boolean> verifyExistence(final Long id) {
        return this.itemRepository.existsById(id).handle((exists, sink) -> {
            if (Boolean.FALSE.equals(exists)) sink.error(new ItemNotFoundException(id));
            else sink.next(exists);
        });
    }
}
