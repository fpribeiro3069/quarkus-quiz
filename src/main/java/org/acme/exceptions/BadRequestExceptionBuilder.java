package org.acme.exceptions;

import jakarta.ws.rs.BadRequestException;

public class BadRequestExceptionBuilder {

    private BadRequestExceptionBuilder() {
    }

    public static BadRequestException questionNotFoundForQuizCreationException() {
        return new BadRequestException("Question id could not be found");
    }

    public static BadRequestException quizAlreadyFinishedException() {
        return new BadRequestException("Quiz is already finished");
    }
}
