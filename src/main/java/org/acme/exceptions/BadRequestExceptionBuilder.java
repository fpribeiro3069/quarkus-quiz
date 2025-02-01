package org.acme.exceptions;

import jakarta.ws.rs.BadRequestException;

public class BadRequestExceptionBuilder {

    private BadRequestExceptionBuilder() {
    }

    public static BadRequestException noCorrectSelectionException() {
        return new BadRequestException("Question can't have no correct selection(s)");
    }

    public static BadRequestException lessThanTwoSelectionsException() {
        return new BadRequestException("Question can't have less than 2 selections");
    }

    public static BadRequestException questionNotFoundForQuizCreationException() {
        return new BadRequestException("Question id could not be found");
    }

    public static BadRequestException quizAlreadyFinishedException() {
        return new BadRequestException("Quiz is already finished");
    }

    public static BadRequestException quizNotFinishedYetException() {
        return new BadRequestException("Quiz is not finished yet");
    }
}
