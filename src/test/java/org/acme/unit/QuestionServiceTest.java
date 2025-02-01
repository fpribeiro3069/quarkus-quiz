//package org.acme.unit;
//
//import io.quarkus.test.Mock;
//import org.acme.entity.db.Question;
//import org.acme.entity.db.Selection;
//import org.acme.service.QuestionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.reset;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class QuestionServiceTest {
//
//    @Mock
//    private Question question; // Mock the Question entity
//
//    @Mock
//    private Selection selection1; // Mock the Selection entity
//
//    @Mock
//    private Selection selection2; // Mock the Selection entity
//
//    @InjectMocks
//    private QuestionService questionService; // Inject mocks into the service
//
//    @BeforeEach
//    public void setUp() {
//        // Reset mocks before each test
//        reset(question, selection1, selection2);
//    }
//
//    @Test
//    @DisplayName("Create a valid question with at least two selections and one correct selection should succeed")
//    public void testCreateValidQuestion() {
//        // Given
//        when(question.getSelections()).thenReturn(Arrays.asList(selection1, selection2));
//        when(selection1.isCorrect()).thenReturn(true); // At least one correct selection
//        when(selection2.isCorrect()).thenReturn(false);
//
//        // When
//        Question result = questionService.create(question);
//
//        // Then
//        assertNotNull(result);
//        verify(question).persist(); // Verify that persist was called
//        verify(selection1).persist(); // Verify that selections were persisted
//        verify(selection2).persist();
//    }
//
//    @Test
//    @DisplayName("Create a question with less than two selections should throw an exception")
//    public void testCreateQuestionWithLessThanTwoSelections() {
//        // Given
//        when(question.getSelections()).thenReturn(Collections.singletonList(selection1)); // Only one selection
//
//        // When / Then
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            questionService.create(question);
//        });
//
//        assertEquals("A question must have at least two selections", exception.getMessage());
//        verify(question, never()).persist(); // Verify that persist was never called
//        verify(selection1, never()).persist();
//    }
//
//    @Test
//    @DisplayName("Create a question with no correct selection should throw an exception")
//    public void testCreateQuestionWithNoCorrectSelection() {
//        // Given
//        when(question.getSelections()).thenReturn(Arrays.asList(selection1, selection2));
//        when(selection1.isCorrect()).thenReturn(false); // No correct selection
//        when(selection2.isCorrect()).thenReturn(false);
//
//        // When / Then
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            questionService.create(question);
//        });
//
//        assertEquals("A question must have at least one correct selection", exception.getMessage());
//        verify(question, never()).persist(); // Verify that persist was never called
//        verify(selection1, never()).persist();
//        verify(selection2, never()).persist();
//    }
//
//    @Test
//    @DisplayName("Create a question with null selections should throw an exception")
//    public void testCreateQuestionWithNullSelections() {
//        // Given
//        when(question.getSelections()).thenReturn(null); // Null selections
//
//        // When / Then
//        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
//            questionService.create(question);
//        });
//
//        assertEquals("Selections cannot be null", exception.getMessage());
//        verify(question, never()).persist(); // Verify that persist was never called
//    }
//}
