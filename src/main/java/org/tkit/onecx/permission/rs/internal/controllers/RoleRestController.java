package org.tkit.onecx.permission.rs.internal.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.rs.internal.mappers.RoleMapper;
import org.tkit.quarkus.jpa.exceptions.ConstraintException;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.internal.RoleInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.CreateRoleRequestDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.RoleSearchCriteriaDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.UpdateRoleRequestDTO;

@LogService
@ApplicationScoped
public class RoleRestController implements RoleInternalApi {

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    RoleDAO dao;

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    RoleMapper mapper;

    @Context
    UriInfo uriInfo;

    @Override
    public Response createRole(CreateRoleRequestDTO createRoleRequestDTO) {
        var role = mapper.create(createRoleRequestDTO);
        role = dao.create(role);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path(role.getId()).build())
                .entity(mapper.map(role)).build();
    }

    @Override
    public Response deleteRole(String id) {
        assignmentDAO.deleteByCriteria(id, null, null, null);
        dao.deleteQueryById(id);
        return Response.noContent().build();
    }

    @Override
    public Response getRoleById(String id) {
        var role = dao.findById(id);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(mapper.map(role)).build();
    }

    @Override
    public Response searchRoles(RoleSearchCriteriaDTO roleSearchCriteriaDTO) {
        var criteria = mapper.map(roleSearchCriteriaDTO);
        var result = dao.findByCriteria(criteria);
        return Response.ok(mapper.mapPage(result)).build();
    }

    @Override
    public Response updateRole(String id, UpdateRoleRequestDTO updateRoleRequestDTO) {
        var role = dao.findById(id);
        if (role == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        mapper.update(updateRoleRequestDTO, role);
        dao.update(role);
        return Response.ok(mapper.map(role)).build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(ConstraintException ex) {
        return exceptionMapper.exception(ex);
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> exception(OptimisticLockException ex) {
        return exceptionMapper.optimisticLock(ex);
    }
}
