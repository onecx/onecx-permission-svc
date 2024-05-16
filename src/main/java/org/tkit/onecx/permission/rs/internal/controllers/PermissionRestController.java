package org.tkit.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.services.PermissionService;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.rs.internal.mappers.PermissionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.internal.PermissionInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.CreatePermissionRequestDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.PermissionSearchCriteriaDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.UpdatePermissionRequestDTO;

@LogService
@ApplicationScoped
public class PermissionRestController implements PermissionInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    PermissionMapper mapper;

    @Inject
    PermissionDAO dao;

    @Context
    UriInfo uriInfo;

    @Inject
    PermissionService service;

    @Override
    public Response createPermission(CreatePermissionRequestDTO createPermissionRequestDTO) {
        var permission = mapper.create(createPermissionRequestDTO);
        permission = dao.create(permission);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(permission.getId()).build())
                .entity(mapper.map(permission)).build();
    }

    @Override
    public Response deletePermission(String id) {
        service.deletePermission(id);
        return Response.noContent().build();
    }

    @Override
    public Response getPermission(String id) {
        var data = dao.findById(id);
        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(data)).build();
    }

    @Override
    public Response searchPermissions(PermissionSearchCriteriaDTO permissionSearchCriteriaDTO) {
        var criteria = mapper.map(permissionSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
    }

    @Override
    public Response updatePermission(String id, UpdatePermissionRequestDTO updatePermissionRequestDTO) {
        var permission = dao.findById(id);
        if (permission == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        mapper.update(updatePermissionRequestDTO, permission);
        dao.update(permission);
        return Response.ok(mapper.map(permission)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }
}
