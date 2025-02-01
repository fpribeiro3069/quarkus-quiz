package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
public class AnswerableQuestion extends PanacheEntity {

    public AnswerableQuestion() {
    }

    public AnswerableQuestion(Question linkedQuestion) {
        this.linkedQuestion = linkedQuestion;
    }

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    public Question linkedQuestion;

    @ManyToOne
    public Selection chosenSelection;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    public Question getLinkedQuestion() {
        return linkedQuestion;
    }

    public void setLinkedQuestion(Question linkedQuestion) {
        this.linkedQuestion = linkedQuestion;
    }

    public Selection getChosenSelection() {
        return chosenSelection;
    }

    public void setChosenSelection(Selection chosenSelection) {
        this.chosenSelection = chosenSelection;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
