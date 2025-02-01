package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Quiz extends PanacheEntity {

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    public List<AnswerableQuestion> questions;

    public boolean isFinished;

    public LocalDateTime finishedAt;

    @CreationTimestamp
    LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<AnswerableQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<AnswerableQuestion> questions) {
        this.questions = questions;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
