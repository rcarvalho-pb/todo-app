package com.application.todo.domain.person;

import com.application.todo.domain.person.dto.PersonDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

import java.time.LocalDateTime;

@Table
@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
public class Person {

    @Id
    private Long id;

    @Version
    private Long version;

    @NotBlank
    @Size(max=100)
    private String firstName;

    @NotBlank
    @Size(max=100)
    private String lastName;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Person(PersonDTO personDTO) {
        this.id = personDTO.id();
        this.firstName = personDTO.firstName();
        this.lastName = personDTO.lastName();
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
    }

    public static PersonDTO toDTO(Person person) {
        return new PersonDTO(
                person.getId(),
                person.getFirstName(),
                person.getLastName()
        );
    }
}
