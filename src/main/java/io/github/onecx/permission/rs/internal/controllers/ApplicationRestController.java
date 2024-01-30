package io.github.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.permission.rs.internal.ApplicationInternalApi;
import gen.io.github.onecx.permission.rs.internal.model.ApplicationSearchCriteriaDTO;
import gen.io.github.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import io.github.onecx.permission.domain.daos.ApplicationDAO;
import io.github.onecx.permission.rs.internal.mappers.ApplicationMapper;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;

@LogService
@ApplicationScoped
public class ApplicationRestController implements ApplicationInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    ApplicationMapper mapper;

    @Inject
    ApplicationDAO dao;

    @Override
    public Response searchApplications(ApplicationSearchCriteriaDTO applicationSearchCriteriaDTO) {
        var criteria = mapper.map(applicationSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
