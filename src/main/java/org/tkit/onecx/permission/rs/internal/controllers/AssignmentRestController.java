package org.tkit.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.domain.services.AssignmentService;
import org.tkit.onecx.permission.rs.internal.mappers.AssignmentMapper;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.internal.AssignmentInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.*;

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

    @Inject
    AssignmentService service;

    @Override
    public Response getAssignment(String id) {
        var data = dao.findById(id);
        if (data == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(data)).build();
    }

    @Override
    public Response searchAssignments(AssignmentSearchCriteriaDTO assignmentSearchCriteriaDTO) {
        var criteria = mapper.map(assignmentSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.map(result)).build();
    }

    @Override
    public Response createAssignment(CreateAssignmentRequestDTO createAssignmentRequestDTO) {
        var role = roleDAO.findById(createAssignmentRequestDTO.getRoleId());
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var permission = permissionDAO.findById(createAssignmentRequestDTO.getPermissionId());
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
    public Response grantRoleAssignments(String roleId) {
        var role = roleDAO.findById(roleId);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var permissions = permissionDAO.findAll().toList();
        if (permissions.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var data = mapper.createList(role, permissions);
        service.createAssignments(role, data);
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response grantRoleApplicationAssignments(String roleId,
            CreateRoleProductAssignmentRequestDTO createRoleProductAssignmentRequestDTO) {
        var role = roleDAO.findById(roleId);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var permissions = permissionDAO.findByProductAndAppId(createRoleProductAssignmentRequestDTO.getProductName(),
                createRoleProductAssignmentRequestDTO.getAppId());
        if (permissions.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var data = mapper.createList(role, permissions);
        service.createRoleProductAssignments(role, createRoleProductAssignmentRequestDTO.getProductName(),
                createRoleProductAssignmentRequestDTO.getAppId(), data);
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response grantRoleProductsAssignments(String roleId,
            CreateRoleProductsAssignmentRequestDTO createRoleProductsAssignmentRequestDTO) {
        var role = roleDAO.findById(roleId);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var permissions = permissionDAO.findByProductNames(createRoleProductsAssignmentRequestDTO.getProductNames());
        if (permissions.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var data = mapper.createList(role, permissions);
        service.createRoleProductsAssignments(role, createRoleProductsAssignmentRequestDTO.getProductNames(), data);
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response revokeRoleAssignments(String roleId) {
        var role = roleDAO.findById(roleId);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        dao.deleteByRoleId(roleId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response revokeRoleApplicationAssignments(String roleId,
            RevokeRoleProductAssignmentRequestDTO revokeRoleProductAssignmentRequestDTO) {
        var role = roleDAO.findById(roleId);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var permissions = permissionDAO.findByProductAndAppId(revokeRoleProductAssignmentRequestDTO.getProductName(),
                revokeRoleProductAssignmentRequestDTO.getAppId());
        if (permissions.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        dao.deleteByProductNameAppId(role.getId(),
                revokeRoleProductAssignmentRequestDTO.getProductName(), revokeRoleProductAssignmentRequestDTO.getAppId());

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response revokeRoleProductsAssignments(String roleId,
            RevokeRoleProductsAssignmentRequestDTO revokeRoleProductsAssignmentRequestDTO) {
        var role = roleDAO.findById(roleId);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var permissions = permissionDAO.findByProductNames(revokeRoleProductsAssignmentRequestDTO.getProductNames());
        if (permissions.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        dao.deleteByProducts(role.getId(), revokeRoleProductsAssignmentRequestDTO.getProductNames());
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @Override
    public Response deleteAssignment(String id) {
        var assignment = dao.findById(id);
        if (assignment != null && !Boolean.TRUE.equals(assignment.getMandatory())) {
            dao.deleteQueryById(id);
        }
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
