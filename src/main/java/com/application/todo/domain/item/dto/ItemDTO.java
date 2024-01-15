package com.application.todo.domain.item.dto;

import com.application.todo.domain.item.enums.ItemStatus;
import com.application.todo.domain.person.dto.PersonDTO;
import com.application.todo.domain.tag.Tag;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Accessors(chain = true)
public record ItemDTO (
        Optional<Long> id,
        Optional<Long> version,
        Optional<String> description,
        Optional<ItemStatus> status,
        Optional<Long> assigneeId,
        Optional<PersonDTO> assignee,
        Optional<List<Tag>> tags,
        Optional<LocalDateTime> createdDate,
        Optional<LocalDateTime> lastModifiedDate
) {}
