package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;
import org.acme.entity.Question;
import org.acme.entity.Selection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.acme.utils.CreationUtils.createAndPersistTestQuestion;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class QuestionResourceTest {

    @BeforeEach
    @Transactional
    public void setup() {
        // Clear the database before each test
        Selection.deleteAll();
        Question.deleteAll();
    }

    @Test
    @DisplayName("List all questions should return 200 and a list of persisted questions")
    public void testListQuestions() {
        // Given
        createAndPersistTestQuestion("What is Quarkus?", List.of(new Selection("A", true), new Selection("B")));
        createAndPersistTestQuestion("What is REST?", List.of(new Selection("C"), new Selection("D", true)));

        // When
        Response response = given()
                .when()
                .get("/question")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Then
        List<Question> questions = response.jsonPath().getList("", Question.class);
        assertNotNull(questions);
        assertEquals(2, questions.size());
        assertTrue(questions.stream().anyMatch(q -> q.text.equals("What is Quarkus?")));
        assertTrue(questions.stream().anyMatch(q -> q.text.equals("What is REST?")));
    }

    @Test
    @DisplayName("Get a question by ID should return 200 and the correct question")
    public void testGetQuestion() {
        // Given
        Question question = createAndPersistTestQuestion("What is Quarkus?", List.of(new Selection("A", true), new Selection("B")));

        // When
        Response response = given()
                .pathParam("id", question.id)
                .when()
                .get("/question/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Then
        Question retrievedQuestion = response.as(Question.class);
        assertNotNull(retrievedQuestion);
        assertEquals(question.text, retrievedQuestion.text);
        assertEquals(question.selections.size(), retrievedQuestion.selections.size());
    }

    @Test
    @DisplayName("Create a valid question should return 200 and persist the question")
    public void testCreateQuestion() {
        // Given
        Question question = new Question();
        question.text = "What is Quarkus?";
        question.selections = List.of(new Selection("A", true), new Selection("B"));

        // When
        Response response = given()
                .contentType(ContentType.JSON)
                .body(question)
                .when()
                .post("/question")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response();

        // Then
        Question createdQuestion = response.as(Question.class);
        assertNotNull(createdQuestion.id);
        assertEquals(question.text, createdQuestion.text);
        assertEquals(question.selections.size(), createdQuestion.selections.size());

        // Verify that the question is persisted in the database
        Question persistedQuestion = Question.findById(createdQuestion.id);
        assertNotNull(persistedQuestion);
        assertEquals(question.text, persistedQuestion.text);
        assertEquals(question.selections.size(), persistedQuestion.selections.size());
    }

    @Test
    @DisplayName("Create a question with missing text and selections should return 400 with validation errors")
    public void testCreateQuestionValidationFailure() {
        // Given
        Question invalidQuestion = new Question(); // Missing text and selections

        // When
        given()
                .contentType(ContentType.JSON)
                .body(invalidQuestion)
                .when()
                .post("/question")
                .then()
                .statusCode(400) // Bad Request due to validation errors
                .body("violations.message", hasItems(
                        "Question text can't be empty",
                        "Question selections can't be empty"
                ));
    }

    @Test
    @DisplayName("Create a question with invalid selection (missing text) should return 400 with validation error")
    public void testCreateQuestionSelectionValidationFailure() {
        // Given
        Question invalidQuestion = new Question(); // Missing selection text
        invalidQuestion.text = "What is Quarkus?";
        invalidQuestion.selections = List.of(new Selection());

        // When
        given()
                .contentType(ContentType.JSON)
                .body(invalidQuestion)
                .when()
                .post("/question")
                .then()
                .statusCode(400) // Bad Request due to validation errors
                .body("violations.message", hasItems(
                        "Selection text can't be empty"
                ));
    }

    @Test
    @DisplayName("Get a non-existent question by ID should return 204 (No Content)")
    public void testGetQuestionNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When
        given()
                .pathParam("id", nonExistentId)
                .when()
                .get("/question/{id}")
                .then()
                .statusCode(204);
    }
}