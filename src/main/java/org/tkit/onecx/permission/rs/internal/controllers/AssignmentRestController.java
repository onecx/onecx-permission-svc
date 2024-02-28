package org.tkit.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.rs.internal.mappers.AssignmentMapper;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.internal.AssignmentInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.AssignmentSearchCriteriaDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.CreateRevokeAssignmentRequestDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;

@LogService
@ApplicationScoped
public class AssignmentRestController implements AssignmentInternalApi {

    @Inject
    AssignmentMapper mapper;

    @Inject
    AssignmentDAO dao;

    @Inject
    ExceptionMapper exceptionMapper;

    @Context
    UriInfo uriInfo;

    @Inject
    RoleDAO roleDAO;

    @Inject
    PermissionDAO permissionDAO;

    @Override
    public Response getAssignment(String id) {
        var data = dao.findById(id);
        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(data)).build();
    }

    @Override
    @Transactional
    public Response revokeAssignments(CreateRevokeAssignmentRequestDTO createRevokeAssignmentRequestDTO) {
        var role = roleDAO.findById(createRevokeAssignmentRequestDTO.getRoleId());
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // case 1 ONLY ROLE ID
        if (createRevokeAssignmentRequestDTO.getPermissionId() == null
                && createRevokeAssignmentRequestDTO.getProductNames() == null) {
            dao.deleteByCriteria(role.getId(), null, null);
        }

        // case 2 ROLE ID + PRODUCT NAMES
        if (createRevokeAssignmentRequestDTO.getProductNames() != null) {
            dao.deleteByCriteria(role.getId(), createRevokeAssignmentRequestDTO.getProductNames(), null);
        }

        // case 3 ROLE ID + permissionID
        if (createRevokeAssignmentRequestDTO.getPermissionId() != null) {
            dao.deleteByCriteria(role.getId(), null, createRevokeAssignmentRequestDTO.getPermissionId());
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response searchAssignments(AssignmentSearchCriteriaDTO assignmentSearchCriteriaDTO) {
        var criteria = mapper.map(assignmentSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
    }

    @Override
    @Transactional
    public Response createAssignment(CreateRevokeAssignmentRequestDTO createAssignmentRequestDTO) {
        var role = roleDAO.findById(createAssignmentRequestDTO.getRoleId());
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // single assignment
        if (createAssignmentRequestDTO.getPermissionId() != null) {
            var permission = permissionDAO.findById(createAssignmentRequestDTO.getPermissionId());
            if (permission == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            var data = mapper.create(role, permission);
            data = dao.create(data);
            return Response
                    .created(uriInfo.getAbsolutePathBuilder().path(data.getId()).build())
                    .entity(mapper.mapResponseList(null, data))
                    .build();
        }

        // batch operation for all permissions by productNames
        if (createAssignmentRequestDTO.getProductNames() != null) {
            var permissions = permissionDAO.loadByProductNames(createAssignmentRequestDTO.getProductNames());
            if (permissions.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            var data = mapper.createList(role, permissions);

            dao.deleteByCriteria(role.getId(), createAssignmentRequestDTO.getProductNames(), null);
            var result = dao.create(data).toList();
            return Response.status(Response.Status.CREATED).entity(mapper.mapResponseList(result, null)).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Override
    public Response deleteAssignment(String id) {
        dao.deleteQueryById(id);
        return Response.noContent().build();
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
