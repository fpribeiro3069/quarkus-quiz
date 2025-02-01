package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.entity.AnswerableQuestion;
import org.acme.entity.Question;
import org.acme.entity.Quiz;
import org.acme.entity.Selection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.acme.exceptions.BadRequestExceptionBuilder.questionNotFoundForQuizCreationException;
import static org.acme.exceptions.BadRequestExceptionBuilder.quizAlreadyFinishedException;
import static org.acme.exceptions.NotFoundExceptionBuilder.questionNotFoundException;
import static org.acme.exceptions.NotFoundExceptionBuilder.quizNotFoundException;
import static org.acme.exceptions.NotFoundExceptionBuilder.selectionNotFoundException;

@ApplicationScoped
public class QuizService {

    @Transactional
    public Quiz create(Set<Long> questionIds) {
        List<AnswerableQuestion> answerableQuestions = new ArrayList<>();

        for (Long questionId : questionIds) {
            Optional<Question> persistedQuestion = Question.findByIdOptional(questionId);
            if (persistedQuestion.isEmpty()) {
                throw questionNotFoundForQuizCreationException();
            } else {
                AnswerableQuestion answerableQuestion = new AnswerableQuestion();
                answerableQuestion.linkedQuestion = persistedQuestion.get();
                answerableQuestions.add(answerableQuestion);
            }
        }

        AnswerableQuestion.persist(answerableQuestions);

        Quiz quiz = new Quiz();
        quiz.questions = answerableQuestions;
        quiz.persist();

        return quiz;
    }

    public Quiz read(Long id) {
        return Quiz.findById(id);
    }

    @Transactional
    public Quiz updateQuestionSelection(Long quizId, Long questionId, Long selectionId) {

        // Find quiz
        Optional<Quiz> optionalQuiz = Quiz.findByIdOptional(quizId);

        if (optionalQuiz.isEmpty()) {
            throw quizNotFoundException();
        }

        Quiz quiz = optionalQuiz.get();

        // Check if quiz is already finished
        if (quiz.isFinished) {
            throw quizAlreadyFinishedException();
        }

        // Find answerable question
        Optional<AnswerableQuestion> optionalAnswerableQuestion = quiz.questions.stream()
                .filter(answerableQuestion -> Objects.equals(answerableQuestion.id, questionId))
                .findFirst();

        if (optionalAnswerableQuestion.isEmpty()) {
            throw questionNotFoundException();
        }

        AnswerableQuestion answerableQuestion = optionalAnswerableQuestion.get();

        // Check if selection exists for the question and update
        Optional<Selection> foundSelection = answerableQuestion.linkedQuestion.selections.stream()
                .filter(selection -> selection.id.equals(selectionId))
                .findFirst();

        if (foundSelection.isPresent()) {
            answerableQuestion.chosenSelection = foundSelection.get();
        } else {
            throw selectionNotFoundException();
        }

        answerableQuestion.persist();
        return quiz;
    }

    @Transactional
    public Quiz finishQuiz(Long quizId) {
        // Find quiz
        Optional<Quiz> optionalQuiz = Quiz.findByIdOptional(quizId);

        if (optionalQuiz.isEmpty()) {
            throw quizNotFoundException();
        }

        Quiz quiz = optionalQuiz.get();

        if (quiz.isFinished) {
            throw quizAlreadyFinishedException();
        }

        // Update isFinished
        quiz.isFinished = true;
        quiz.finishedAt = LocalDateTime.now();
        quiz.persist();

        return quiz;
    }
}
