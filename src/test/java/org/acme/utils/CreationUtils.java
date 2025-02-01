package org.acme.utils;

import jakarta.transaction.Transactional;
import org.acme.entity.db.AnswerableQuestion;
import org.acme.entity.db.Question;
import org.acme.entity.db.Quiz;
import org.acme.entity.db.Selection;

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
        return createAndPersistTestQuiz(false, false);
    }

    public static Quiz createAndPersistTestQuiz(boolean isFinished) {
        return createAndPersistTestQuiz(isFinished, false);
    }

    @Transactional
    public static Quiz createAndPersistTestQuiz(boolean isFinished, boolean modifyResponse) {
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

        if (modifyResponse) {
            // Simulate answering some questions correctly and some incorrectly
            AnswerableQuestion answerableQuestion11 = quiz.questions.getFirst();
            answerableQuestion1.chosenSelection = answerableQuestion1.linkedQuestion.selections.stream()
                    .filter(selection -> selection.correct)
                    .findFirst()
                    .orElseThrow();
            answerableQuestion11.persist();

            AnswerableQuestion answerableQuestion22 = quiz.questions.get(1);
            answerableQuestion22.chosenSelection = answerableQuestion2.linkedQuestion.selections.stream()
                    .filter(selection -> !selection.correct)
                    .findFirst()
                    .orElseThrow();
            answerableQuestion2.persist();
        }

        quiz.persist();

        return quiz;
    }
}
