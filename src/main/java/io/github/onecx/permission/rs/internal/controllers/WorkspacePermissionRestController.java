package io.github.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.io.github.onecx.permission.rs.internal.WorkspacePermissionInternalApi;
import gen.io.github.onecx.permission.rs.internal.model.CreateWorkspacePermissionRequestDTO;
import gen.io.github.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.permission.rs.internal.model.UpdateWorkspacePermissionRequestDTO;
import gen.io.github.onecx.permission.rs.internal.model.WorkspacePermissionSearchCriteriaDTO;
import io.github.onecx.permission.domain.daos.WorkspacePermissionDAO;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.permission.rs.internal.mappers.WorkspacePermissionMapper;

@LogService
@ApplicationScoped
public class WorkspacePermissionRestController implements WorkspacePermissionInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    WorkspacePermissionMapper mapper;

    @Inject
    WorkspacePermissionDAO dao;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createWorkspacePermission(CreateWorkspacePermissionRequestDTO createWorkspacePermissionRequestDTO) {
        var item = mapper.create(createWorkspacePermissionRequestDTO);
        item = dao.create(item);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(item.getId()).build())
                .entity(mapper.map(item))
                .build();
    }

    @Override
    public Response deleteWorkspacePermission(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getWorkspacePermissionById(String id) {
        var data = dao.findById(id);
        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(data)).build();
    }

    @Override
    public Response searchWorkspacePermissions(WorkspacePermissionSearchCriteriaDTO workspacePermissionSearchCriteriaDTO) {
        var criteria = mapper.map(workspacePermissionSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
    }

    @Override
    public Response updateWorkspacePermission(String id,
            UpdateWorkspacePermissionRequestDTO updateWorkspacePermissionRequestDTO) {
        var item = dao.findById(id);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateWorkspacePermissionRequestDTO, item);
        dao.update(item);
        return Response.ok(mapper.map(item)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }
}
