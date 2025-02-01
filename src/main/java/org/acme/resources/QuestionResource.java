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
import org.acme.entity.Question;
import org.acme.service.QuestionService;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/question")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuestionResource {

    @Inject
    QuestionService questionService;

    @GET
    public List<Question> listQuestions() {
        return questionService.list();
    }

    @GET
    @Path("/{id}")
    public Question getQuestion(Long id) {
        return questionService.read(id);
    }

    @POST
    public RestResponse<Question> createQuestion(@Valid Question question, @Context UriInfo uriInfo) {
        Question savedQuestion = questionService.create(question);

        return RestResponse.seeOther(uriInfo.getAbsolutePathBuilder().path(Long.toString(savedQuestion.id)).build());
    }
}
