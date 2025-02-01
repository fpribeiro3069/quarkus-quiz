package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class Selection extends PanacheEntity {

    @NotEmpty(message = "Selection text can't be empty")
    String text;

    boolean correct;

    public Selection() {
    }

    public Selection(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public Selection(String text) {
        this.text = text;
        this.correct = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
