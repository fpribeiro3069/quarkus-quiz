package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Quiz extends PanacheEntity {

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    public List<AnswerableQuestion> questions;

    public boolean isFinished;

    public LocalDateTime finishedAt;

    @CreationTimestamp
    LocalDateTime createdAt;
}
