package org.acme.resources;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.acme.entity.db.Question;
import org.acme.service.QuestionService;
import org.jboss.resteasy.reactive.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestionResource {

    private static final Logger log = LoggerFactory.getLogger(QuestionResource.class);
    @Inject
    QuestionService questionService;

    @GET
    public List<Question> listQuestions() {
        log.info("listQuestions. request to list all questions");

        return questionService.list();
    }

    @GET
    @Path("/{id}")
    public Question getQuestion(Long id) {
        log.info("getQuestion. request to get question with id {}", id);

        return questionService.read(id);
    }

    @POST
    public RestResponse<Question> createQuestion(@Valid Question question, @Context UriInfo uriInfo) {
        log.info("createQuestion. request to create question with question={}", question);

        Question savedQuestion = questionService.create(question);

        return RestResponse.seeOther(uriInfo.getAbsolutePathBuilder().path(Long.toString(savedQuestion.id)).build());
    }
}
