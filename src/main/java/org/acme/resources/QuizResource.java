package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.acme.entity.db.Quiz;
import org.acme.entity.responses.ResultResponse;
import org.acme.service.QuizService;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Path("/quiz")
public class QuizResource {

    private static final Logger log = LoggerFactory.getLogger(QuizResource.class);

    @Inject
    QuizService quizService;

    @POST
    public RestResponse<Quiz> createQuiz(@NotEmpty(message = "Question list can't be empty")
                               Set<Long> questionIds,
                                         @Context UriInfo uriInfo) {
        log.info("createQuiz. request to create quiz {}", questionIds);

        Quiz quiz = quizService.create(questionIds);

        return RestResponse.seeOther(uriInfo.getAbsolutePathBuilder().path(Long.toString(quiz.id)).build());
    }

    @GET
    @Path("/{quizId}")
    public Quiz getQuiz(Long quizId) {
        log.info("getQuiz. request to read quiz with id {}", quizId);

        return quizService.read(quizId);
    }

    @PATCH
    @Path("/{quizId}/question/{questionId}")
    public Quiz submitSelectionToQuestion(Long quizId, Long questionId, Long selectionId) {
        log.info("submitSelectionToQuestion. request to update quiz question selection quizId={}, questionId={}, selectionId={}",
                quizId, questionId, selectionId);

        return quizService.updateQuestionSelection(quizId, questionId, selectionId);
    }

    @POST
    @Path("/{quizId}")
    public RestResponse<Quiz> finishQuiz(Long quizId, @Context UriInfo uriInfo) {
        log.info("finishQuiz. request to finish quiz with id {}", quizId);

        quizService.finishQuiz(quizId);

        return RestResponse.seeOther(uriInfo.getAbsolutePathBuilder().build());
    }

    @GET
    @Path("/{quizId}/result")
    public ResultResponse getQuizResult(Long quizId) {
        log.info("getQuizResult. request to get result for quiz {}", quizId);

        return quizService.getResult(quizId);
    }
}
