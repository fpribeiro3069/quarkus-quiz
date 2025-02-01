package org.acme.exceptions.mappers;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.acme.exceptions.ApplicationException;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(e.getResponse().getStatus())
                .entity(new ApplicationException(e.getMessage()))
                .build();
    }
}
