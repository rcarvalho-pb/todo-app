package com.application.todo.domain.tag;

import com.application.todo.domain.tag.dto.TagDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Table
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class Tag {

    @Id
    private Long id;

    @Version
    private Long version;

    @NotBlank
    @Size(max=100)
    private String name;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Tag(TagDTO tagDTO) {
        this.id = tagDTO.id();
        this.name = tagDTO.name();
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    public static TagDTO toDTO(Tag tag) {
        return new TagDTO(tag.getId(), tag.getName());
    }
}
