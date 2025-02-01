package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
public class Question extends PanacheEntity {

    @NotEmpty(message = "Question text can't be empty")
    public String text;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "question_id", nullable = false)
    @NotEmpty(message = "Question selections can't be empty")
    @Valid
    public List<Selection> selections;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Selection> getSelections() {
        return selections;
    }

    public void setSelections(List<Selection> selections) {
        this.selections = selections;
    }
}
