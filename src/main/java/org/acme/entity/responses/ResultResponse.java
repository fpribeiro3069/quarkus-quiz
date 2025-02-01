package org.acme.entity.responses;

public record ResultResponse (
    int totalQuestions,
    long totalAnsweredQuestions,
    long totalCorrectQuestions
){}
