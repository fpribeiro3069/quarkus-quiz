package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AnswerableQuestion extends PanacheEntity {

    public AnswerableQuestion(Question linkedQuestion) {
        this.linkedQuestion = linkedQuestion;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    public Question linkedQuestion;

    @ManyToOne
    public Selection chosenSelection;

    @UpdateTimestamp
    LocalDateTime updatedAt;
}
