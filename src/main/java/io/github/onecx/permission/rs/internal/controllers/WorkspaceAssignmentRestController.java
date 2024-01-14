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

import gen.io.github.onecx.permission.rs.internal.WorkspaceAssignmentInternalApi;
import gen.io.github.onecx.permission.rs.internal.model.CreateWorkspaceAssignmentRequestDTO;
import gen.io.github.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import gen.io.github.onecx.permission.rs.internal.model.WorkspaceAssignmentSearchCriteriaDTO;
import io.github.onecx.permission.domain.daos.RoleDAO;
import io.github.onecx.permission.domain.daos.WorkspaceAssignmentDAO;
import io.github.onecx.permission.domain.daos.WorkspacePermissionDAO;
import io.github.onecx.permission.rs.internal.mappers.ExceptionMapper;
import io.github.onecx.permission.rs.internal.mappers.WorkspaceAssignmentMapper;

@LogService
@ApplicationScoped
public class WorkspaceAssignmentRestController implements WorkspaceAssignmentInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    RoleDAO roleDAO;

    @Inject
    WorkspacePermissionDAO workspacePermissionDAO;

    @Inject
    WorkspaceAssignmentMapper mapper;

    @Context
    UriInfo uriInfo;

    @Inject
    WorkspaceAssignmentDAO dao;

    @Override
    public Response createWorkspaceAssignment(CreateWorkspaceAssignmentRequestDTO createWorkspaceAssignmentRequestDTO) {
        var role = roleDAO.findById(createWorkspaceAssignmentRequestDTO.getRoleId());
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var permission = workspacePermissionDAO.findById(createWorkspaceAssignmentRequestDTO.getPermissionId());
        if (permission == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var data = mapper.create(role, permission);
        data = dao.create(data);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(data.getId()).build())
                .entity(mapper.map(data))
                .build();
    }

    @Override
    public Response deleteWorkspaceAssignment(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getWorkspaceAssignment(String id) {
        var data = dao.findById(id);
        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(data)).build();
    }

    @Override
    public Response searchWorkspaceAssignments(WorkspaceAssignmentSearchCriteriaDTO workspaceAssignmentSearchCriteriaDTO) {
        var criteria = mapper.map(workspaceAssignmentSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
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
