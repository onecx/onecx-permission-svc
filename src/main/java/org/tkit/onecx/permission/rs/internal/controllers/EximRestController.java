package org.tkit.onecx.permission.rs.internal.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.AssignmentDAO;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.onecx.permission.domain.services.AssignmentService;
import org.tkit.onecx.permission.rs.exim.v1.mappers.EximMapperV1;
import org.tkit.onecx.permission.rs.internal.mappers.ExceptionMapper;
import org.tkit.onecx.permission.rs.internal.mappers.EximMapper;

import gen.org.tkit.onecx.permission.rs.internal.EximInternalApi;
import gen.org.tkit.onecx.permission.rs.internal.model.AssignmentSnapshotDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ExportAssignmentsRequestDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailInvalidParamDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailResponseDTO;

@ApplicationScoped
public class EximRestController implements EximInternalApi {

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    EximMapper mapper;

    @Inject
    ExceptionMapper exceptionMapper;

    @Inject
    AssignmentService service;

    @Override
    public Response exportAssignments(ExportAssignmentsRequestDTO exportAssignmentsRequestDTO) {
        var permissionActions = assignmentDAO.findPermissionActionForProducts(exportAssignmentsRequestDTO.getProductNames());
        return Response.ok(mapper.createSnapshot(permissionActions)).build();
    }

    @Override
    public Response importAssignments(AssignmentSnapshotDTO assignmentSnapshotDTO) {

        var request = mapper.createRequestData(assignmentSnapshotDTO);

        // map of roles for assignments
        var roles = roleDAO.findByNames(request.roles());
        var roleMap = roles.stream().collect(Collectors.toMap(Role::getName, x -> x));

        // map of permissions for products
        var permissions = permissionDAO.findByProductNames(request.product().keySet());
        var permissionMap = permissions.stream().collect(Collectors.toMap(EximMapperV1::permId, x -> x));

        // create assignments
        List<ProblemDetailInvalidParamDTO> problems = new ArrayList<>();
        List<Role> createRoles = new ArrayList<>();
        var assignments = mapper.createAssignments(problems, assignmentSnapshotDTO, roleMap, permissionMap, createRoles);

        // delete old and create new assignments
        service.importOperator(assignments, request.product(), createRoles);

        // check problems
        if (!problems.isEmpty()) {
            return exceptionMapper.importError(problems);
        }

        return Response.ok().build();
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetailResponseDTO> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
