package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.entity.db.AnswerableQuestion;
import org.acme.entity.db.Question;
import org.acme.entity.db.Quiz;
import org.acme.entity.db.Selection;
import org.acme.entity.responses.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.acme.exceptions.BadRequestExceptionBuilder.questionNotFoundForQuizCreationException;
import static org.acme.exceptions.BadRequestExceptionBuilder.quizAlreadyFinishedException;
import static org.acme.exceptions.BadRequestExceptionBuilder.quizNotFinishedYetException;
import static org.acme.exceptions.NotFoundExceptionBuilder.questionNotFoundException;
import static org.acme.exceptions.NotFoundExceptionBuilder.quizNotFoundException;
import static org.acme.exceptions.NotFoundExceptionBuilder.selectionNotFoundException;

@ApplicationScoped
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    @Transactional
    public Quiz create(Set<Long> questionIds) {
        List<AnswerableQuestion> answerableQuestions = new ArrayList<>();

        for (Long questionId : questionIds) {
            Optional<Question> persistedQuestion = Question.findByIdOptional(questionId);
            if (persistedQuestion.isEmpty()) {
                log.error("create. question id could not be found. aborting quiz creation. question id={}", questionId);
                throw questionNotFoundForQuizCreationException();
            } else {
                AnswerableQuestion answerableQuestion = new AnswerableQuestion();
                answerableQuestion.linkedQuestion = persistedQuestion.get();
                answerableQuestions.add(answerableQuestion);
            }
        }
        log.debug("create. answerableQuestions={}", answerableQuestions);

        AnswerableQuestion.persist(answerableQuestions);

        Quiz quiz = new Quiz();
        quiz.questions = answerableQuestions;
        quiz.persist();

        log.debug("create. quiz={}", quiz);
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
        log.debug("updateQuestionSelection. quiz={}", quiz);

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
        log.debug("updateQuestionSelection. answerableQuestion={}", answerableQuestion);

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

        log.debug("updateQuestionSelection. final quiz={}", quiz);
        return quiz;
    }

    @Transactional
    public void finishQuiz(Long quizId) {
        // Find quiz
        Optional<Quiz> optionalQuiz = Quiz.findByIdOptional(quizId);

        if (optionalQuiz.isEmpty()) {
            throw quizNotFoundException();
        }

        Quiz quiz = optionalQuiz.get();
        log.debug("finishQuiz. quiz={}", quiz);

        if (quiz.isFinished) {
            throw quizAlreadyFinishedException();
        }

        // Update isFinished
        quiz.isFinished = true;
        quiz.finishedAt = LocalDateTime.now();
        quiz.persist();

        log.debug("finishQuiz. final quiz={}", quiz);
    }

    public ResultResponse getResult(Long quizId) {
        // Find quiz
        Optional<Quiz> optionalQuiz = Quiz.findByIdOptional(quizId);

        if (optionalQuiz.isEmpty()) {
            throw quizNotFoundException();
        }

        Quiz quiz = optionalQuiz.get();
        log.debug("getResult. quiz={}", quiz);

        if (!quiz.isFinished) {
            throw quizNotFinishedYetException();
        }

        // Perform calculations
        final long totalAnsweredQuestions = quiz.questions.stream()
                .filter(answerableQuestion -> answerableQuestion.chosenSelection != null)
                .count();

        final long totalCorrectQuestions = quiz.questions.stream()
                .filter(answerableQuestion -> answerableQuestion.chosenSelection != null)
                .filter(answerableQuestion -> answerableQuestion.chosenSelection.correct)
                .count();


        final ResultResponse resultResponse = new ResultResponse(quiz.questions.size(), totalAnsweredQuestions, totalCorrectQuestions);

        log.debug("getResult. resultResponse={}", resultResponse);
        return resultResponse;
    }
}
