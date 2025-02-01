package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
@ToString
public class Question extends PanacheEntity {

    @NotEmpty(message = "Question text can't be empty")
    String text;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "question_id", nullable = false)
    @NotEmpty(message = "Question selections can't be empty")
    @Valid
    List<Selection> selections;
}
