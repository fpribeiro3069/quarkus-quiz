package org.acme.entity.db;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Selection extends PanacheEntity {

    @NotEmpty(message = "Selection text can't be empty")
    public String text;

    public boolean correct;

    public Selection(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public Selection(String text) {
        this.text = text;
        this.correct = false;
    }
}
