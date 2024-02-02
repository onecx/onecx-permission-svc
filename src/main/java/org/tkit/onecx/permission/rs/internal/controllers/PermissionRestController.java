package org.tkit.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.rs.internal.mappers.PermissionMapper;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.internal.PermissionInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;

@LogService
@ApplicationScoped
public class PermissionRestController implements PermissionInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    PermissionMapper mapper;

    @Inject
    PermissionDAO dao;

    @Override
    public Response searchPermissions(PermissionSearchCriteriaDTO permissionSearchCriteriaDTO) {
        var criteria = mapper.map(permissionSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
