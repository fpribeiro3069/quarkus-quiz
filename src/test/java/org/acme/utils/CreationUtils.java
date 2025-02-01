package org.acme.utils;

import jakarta.transaction.Transactional;
import org.acme.entity.AnswerableQuestion;
import org.acme.entity.Question;
import org.acme.entity.Quiz;
import org.acme.entity.Selection;

import java.util.List;

public class CreationUtils {

    private CreationUtils() {
    }

    @Transactional
    public static Question createAndPersistTestQuestion(String text, List<Selection> selections) {
        Question question = new Question();
        question.text = text;
        question.selections = selections;
        question.persist();
        Selection.persist(question.selections);
        return question;
    }

    public static Quiz createAndPersistTestQuiz() {
        return createAndPersistTestQuiz(false);
    }

    @Transactional
    public static Quiz createAndPersistTestQuiz(boolean isFinished) {
        Question question1 = createAndPersistTestQuestion("Question 1?", List.of(
                new Selection("Selection 1", true),
                new Selection("Selection 2"))
        );

        Question question2 = createAndPersistTestQuestion("Question 2?", List.of(
                new Selection("Selection 3", true),
                new Selection("Selection 4"))
        );

        AnswerableQuestion answerableQuestion1 = new AnswerableQuestion();
        answerableQuestion1.linkedQuestion = question1;
        answerableQuestion1.persist();

        AnswerableQuestion answerableQuestion2 = new AnswerableQuestion();
        answerableQuestion2.linkedQuestion = question2;
        answerableQuestion2.persist();

        Quiz quiz = new Quiz();
        quiz.questions = List.of(answerableQuestion1, answerableQuestion2);
        quiz.isFinished = isFinished;
        quiz.persist();

        return quiz;
    }
}
