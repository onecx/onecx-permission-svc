package org.tkit.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.ApplicationDAO;
import org.tkit.onecx.permission.domain.services.ApplicationService;
import org.tkit.onecx.permission.rs.internal.mappers.ApplicationMapper;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.internal.ApplicationInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.ApplicationSearchCriteriaDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;

@LogService
@ApplicationScoped
public class ApplicationRestController implements ApplicationInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    ApplicationMapper mapper;

    @Inject
    ApplicationDAO dao;

    @Inject
    ApplicationService applicationService;

    @Override
    public Response deleteByApplicationName(String name) {
        var application = dao.loadByName(name);
        if (application == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        applicationService.deleteApplicationAndRelatedPermissionsAndAssignmentsById(application.getId(),
                application.getAppId());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

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
