package com.application.todo.domain.item;

import com.application.todo.domain.item.dto.ItemDTO;
import com.application.todo.domain.item.enums.ItemStatus;
import com.application.todo.domain.person.Person;
import com.application.todo.domain.person.dto.PersonDTO;
import com.application.todo.domain.tag.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Table
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Item {

    @Id
    private Long id;

    @Version
    private Long version;

    @Size(max=4000)
    @NotBlank
    private String description;

    @NotNull
    private ItemStatus status = ItemStatus.TODO;

    private Long assigneeId;

    @Transient
    private Person assignee;

    @Transient
    private List<Tag> tags;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Item(Long id, Long version) {
        this.id = id;
        this.version = version;
    }

    public Item(ItemDTO dto) {
        if (dto.id().isPresent()) this.id = dto.id().get();
        if (dto.version().isPresent()) this.version = dto.version().get();
        if (dto.description().isPresent()) this.description = dto.description().get();
        if (dto.status().isPresent()) this.status = dto.status().get();
        if (dto.assigneeId().isPresent()) this.assigneeId = dto.assigneeId().get();
        if (dto.assignee().isPresent()) this.assignee = new Person(dto.assignee().get());
        if (dto.tags().isPresent()) this.tags = dto.tags().get();
//        if (dto.createdDate().isPresent()) this.createdDate = dto.createdDate().get();
//        else this.createdDate = LocalDateTime.now();
//        if (dto.lastModifiedDate().isPresent()) this.lastModifiedDate = dto.lastModifiedDate().get();
//        else this.lastModifiedDate = LocalDateTime.now();
    }

    public static ItemDTO toDTO(Item item) {
        Optional<Long> id = item.getId() != null ? Optional.of(item.getId()) : Optional.empty();
        Optional<Long> version = item.getVersion() != null ? Optional.of(item.getVersion()) : Optional.empty();
        Optional<Long> assigneeId = item.getAssigneeId() != null ? Optional.of(item.getAssigneeId()) : Optional.empty();
        Optional<PersonDTO> assignee = item.getAssignee() != null ? Optional.of(Person.toDTO(item.getAssignee())) : Optional.empty();
        Optional<List<Tag>> tags = item.getTags() != null ? Optional.of(item.tags) : Optional.empty();
        Optional<LocalDateTime> createdDate = item.getCreatedDate() != null ? Optional.of(item.createdDate) : Optional.empty();
        Optional<LocalDateTime> lastModifiedDate = item.getLastModifiedDate() != null ? Optional.of(item.getLastModifiedDate()) : Optional.empty();
        Optional<String> description = item.getDescription() != null ? Optional.of(item.getDescription()) : Optional.empty();
        Optional<ItemStatus> status = item.getStatus() != null ? Optional.of(item.getStatus()) : Optional.empty();

        return new ItemDTO(
          id,
          version,
          description,
          status,
          assigneeId,
          assignee,
          tags,
          createdDate,
          lastModifiedDate
        );
    }
}
