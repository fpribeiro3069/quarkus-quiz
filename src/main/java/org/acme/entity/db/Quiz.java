package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Quiz extends PanacheEntity {

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    List<AnswerableQuestion> questions;

    boolean isFinished;

    LocalDateTime finishedAt;

    @CreationTimestamp
    LocalDateTime createdAt;
}
