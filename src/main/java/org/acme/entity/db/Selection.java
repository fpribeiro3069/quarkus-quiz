package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
@ToString
public class Selection extends PanacheEntity {

    @NotEmpty(message = "Selection text can't be empty")
    String text;

    boolean correct;

    public Selection(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public Selection(String text) {
        this.text = text;
        this.correct = false;
    }
}
