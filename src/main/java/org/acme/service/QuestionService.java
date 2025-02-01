package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.acme.entity.Question;
import org.acme.entity.Selection;

import java.util.List;

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
        question.persist();
        Selection.persist(question.selections);
        return question;
    }
}
