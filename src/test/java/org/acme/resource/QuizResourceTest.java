package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import org.acme.entity.db.AnswerableQuestion;
import org.acme.entity.db.Question;
import org.acme.entity.db.Quiz;
import org.acme.entity.db.Selection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.acme.utils.CreationUtils.createAndPersistTestQuestion;
import static org.acme.utils.CreationUtils.createAndPersistTestQuiz;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class QuizResourceTest {

    @Transactional
    @BeforeEach
    void setUp() {
        AnswerableQuestion.deleteAll();
        Selection.deleteAll();
        Question.deleteAll();
        Quiz.deleteAll();
    }

    @Test
    @DisplayName("Create a quiz with valid questions should return 200 and persist the quiz")
    public void testCreateQuiz() {
        // Given
        Question question1 = createAndPersistTestQuestion("What is Quarkus?", List.of(new Selection("A", true), new Selection("B")));
        Question question2 = createAndPersistTestQuestion("What is REST?", List.of(new Selection("C"), new Selection("D", true)));

        List<Question> questions = List.of(question1, question2);

        // When
        Response response = given()
                .contentType(ContentType.JSON)
                .body(questions.stream()
                        .map(question -> question.id)
                        .toList())
                .when()
                .post("/quiz")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Then
        Quiz createdQuiz = response.as(Quiz.class);
        assertNotNull(createdQuiz.id);
        assertNotNull(createdQuiz.getCreatedAt());
        assertEquals(questions.size(), createdQuiz.questions.size());
        assertFalse(createdQuiz.isFinished);

        // Verify that the quiz and answerable question is persisted in the database
        Quiz persistedQuiz = Quiz.findById(createdQuiz.id);
        assertNotNull(persistedQuiz);

        List<Long> linkedQuestions = persistedQuiz.questions.stream()
                .map(answerableQuestion -> answerableQuestion.linkedQuestion.id)
                .toList();

        assertTrue(linkedQuestions.contains(question1.id));
        assertTrue(linkedQuestions.contains(question2.id));
        assertEquals(questions.size(), linkedQuestions.size());
    }

    @Test
    @DisplayName("Create a quiz with an empty question list should return 400 with validation error")
    public void testCreateEmptyQuiz() {
        // When
        given()
                .contentType(ContentType.JSON)
                .body(Set.of())
                .when()
                .post("/quiz")
                .then()
                .statusCode(400) // Bad Request due to validation errors
                .body("violations.message", hasItems(
                        "Question list can't be empty"
                ));
    }

    @Test
    @DisplayName("Create a quiz with a non-existent question ID should return 412 with error message")
    public void testCreateNonExistentQuestionQuiz() {
        Question question1 = createAndPersistTestQuestion("What is Quarkus?", List.of(new Selection("A", true), new Selection("B")));
        Question question2 = createAndPersistTestQuestion("What is REST?", List.of(new Selection("C"), new Selection("D", true)));

        List<Question> questions = List.of(question1, question2);

        List<Long> questionIds = questions.stream()
                .map(question -> question.id)
                .collect(Collectors.toCollection(ArrayList::new));

        questionIds.add(999L);

        // When
        given()
                .contentType(ContentType.JSON)
                .body(questionIds)
                .when()
                .post("/quiz")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("message", is("Question id could not be found"));
    }

    @Test
    @DisplayName("Submit a valid selection to a question in a quiz should return 200 and update the quiz")
    public void testSubmitValidSelection() {
        // Given
        Quiz quiz = createAndPersistTestQuiz();

        Long quizId = quiz.id;
        Long questionId = quiz.questions.getFirst().id;
        Long selectionId = quiz.questions.getFirst().linkedQuestion.selections.getFirst().id;

        // When
        given()
                .contentType(ContentType.JSON)
                .pathParam("quizId", quizId)
                .pathParam("questionId", questionId)
                .body(selectionId)
                .when()
                .patch("/quiz/{quizId}/question/{questionId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(quizId.intValue()));

        // Then
        Quiz updatedQuiz = Quiz.findById(quizId);
        assertNotNull(updatedQuiz);
        Optional<AnswerableQuestion> updatedAnswerableQuestion = updatedQuiz.questions.stream()
                .filter(aq -> aq.id.equals(questionId))
                .findFirst();
        assertTrue(updatedAnswerableQuestion.isPresent());
        assertEquals(selectionId, updatedAnswerableQuestion.get().chosenSelection.id);
    }

    @Test
    @DisplayName("Submit a selection to a non-existent quiz should return 404 with error message")
    public void testSubmitSelectionToNonExistentQuiz() {
        // Given
        Long nonExistentQuizId = 999L;
        Long questionId = 1L;
        Long selectionId = 1L;

        // When
        given()
                .contentType(ContentType.JSON)
                .pathParam("quizId", nonExistentQuizId)
                .pathParam("questionId", questionId)
                .body(selectionId)
                .when()
                .patch("/quiz/{quizId}/question/{questionId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Quiz could not be found"));
    }

    @Test
    @DisplayName("Submit a selection to a non-existent question in a quiz should return 404 with error message")
    public void testSubmitSelectionToNonExistentQuestion() {
        // Given
        Quiz quiz = createAndPersistTestQuiz();

        Long quizId = quiz.id;
        Long nonExistentQuestionId = 999L;
        Long selectionId = quiz.questions.getFirst().linkedQuestion.selections.getFirst().id;

        // When
        given()
                .contentType(ContentType.JSON)
                .pathParam("quizId", quizId)
                .pathParam("questionId", nonExistentQuestionId)
                .body(selectionId)
                .when()
                .patch("/quiz/{quizId}/question/{questionId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Question could not be found"));
    }

    @Test
    @DisplayName("Submit a non-existent selection to a question in a quiz should return 404 with error message")
    public void testSubmitNonExistentSelection() {
        // Given
        Quiz quiz = createAndPersistTestQuiz();

        Long quizId = quiz.id;
        Long questionId = quiz.questions.getFirst().id;
        Long nonExistentSelectionId = 999L;

        // When
        given()
                .contentType(ContentType.JSON)
                .pathParam("quizId", quizId)
                .pathParam("questionId", questionId)
                .body(nonExistentSelectionId)
                .when()
                .patch("/quiz/{quizId}/question/{questionId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Selection could not be found"));
    }

    @Test
    @DisplayName("Submit a selection to a question in a finished quiz should return 400 with error message")
    public void testSubmitSelectionToFinishedQuiz() {
        // Given
        Quiz quiz = createAndPersistTestQuiz(true);

        Long quizId = quiz.id;
        Long questionId = quiz.questions.getFirst().id;
        Long selectionId = quiz.questions.getFirst().linkedQuestion.selections.getFirst().id;

        // When
        given()
                .contentType(ContentType.JSON)
                .pathParam("quizId", quizId)
                .pathParam("questionId", questionId)
                .body(selectionId)
                .when()
                .patch("/quiz/{quizId}/question/{questionId}")
                .then()
                .statusCode(400)
                .body("message", equalTo("Quiz is already finished"));
    }
}