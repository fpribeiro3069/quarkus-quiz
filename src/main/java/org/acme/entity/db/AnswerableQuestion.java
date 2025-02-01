package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class AnswerableQuestion extends PanacheEntity {

    public AnswerableQuestion(Question linkedQuestion) {
        this.linkedQuestion = linkedQuestion;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    Question linkedQuestion;

    @ManyToOne
    Selection chosenSelection;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
