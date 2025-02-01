package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.acme.entity.db.Question;
import org.acme.entity.db.Selection;

import java.util.List;

import static org.acme.exceptions.BadRequestExceptionBuilder.lessThanTwoSelectionsException;
import static org.acme.exceptions.BadRequestExceptionBuilder.noCorrectSelectionException;

@ApplicationScoped
public class QuestionService {

    public List<Question> list() {
        return Question.listAll();
    }

    public Question read(Long id) {
        return Question.findById(id);
    }

    @Transactional
    public Question create(@Valid Question question) {
        if (question.selections.size() < 2) {
            throw lessThanTwoSelectionsException();
        }

        if (question.selections.stream().noneMatch(Selection::isCorrect)) {
            throw noCorrectSelectionException();
        }

        question.persist();
        Selection.persist(question.selections);
        return question;
    }
}
