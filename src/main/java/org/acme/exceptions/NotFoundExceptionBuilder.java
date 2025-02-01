package org.acme.exceptions;

import jakarta.ws.rs.NotFoundException;

public class NotFoundExceptionBuilder {

    private NotFoundExceptionBuilder() {
    }

    public static NotFoundException quizNotFoundException() {
        return new NotFoundException("Quiz could not be found");
    }

    public static NotFoundException questionNotFoundException() {
        return new NotFoundException("Question could not be found");
    }

    public static NotFoundException selectionNotFoundException() {
        return new NotFoundException("Selection could not be found");
    }
}
