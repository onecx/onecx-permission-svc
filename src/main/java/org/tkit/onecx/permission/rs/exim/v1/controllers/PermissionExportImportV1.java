package org.tkit.onecx.permission.rs.exim.v1.controllers;

import java.util.*;
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
import org.tkit.onecx.permission.rs.exim.v1.mappers.EximExceptionMapperV1;
import org.tkit.onecx.permission.rs.exim.v1.mappers.EximMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.exim.v1.PermissionExportImportApi;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.AssignmentSnapshotDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.EximProblemDetailInvalidParamDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.EximProblemDetailResponseDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.ExportAssignmentsRequestDTOV1;

@LogService
@ApplicationScoped
public class PermissionExportImportV1 implements PermissionExportImportApi {

    @Inject
    RoleDAO roleDAO;

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    EximMapperV1 mapper;

    @Inject
    EximExceptionMapperV1 exceptionMapper;

    @Inject
    AssignmentService service;

    @Inject
    AssignmentDAO assignmentDAO;

    @Override
    public Response exportAssignments(ExportAssignmentsRequestDTOV1 exportAssignmentsRequestDTOV1) {
        var permissionActions = assignmentDAO.findPermissionActionForProducts(exportAssignmentsRequestDTOV1.getProductNames());
        return Response.ok(mapper.createSnapshot(permissionActions)).build();
    }

    @Override
    public Response importAssignments(AssignmentSnapshotDTOV1 assignmentSnapshotDTOV1) {
        return operatorImportAssignments(assignmentSnapshotDTOV1);
    }

    @Override
    public Response operatorImportAssignments(AssignmentSnapshotDTOV1 assignmentSnapshotDTO) {

        var request = mapper.createRequestData(assignmentSnapshotDTO);

        // map of roles for assignments
        var roles = roleDAO.findByNames(request.roles());
        var roleMap = roles.stream().collect(Collectors.toMap(Role::getName, x -> x));

        // map of permissions for products
        var permissions = permissionDAO.findByProductNames(request.product().keySet());
        var permissionMap = permissions.stream().collect(Collectors.toMap(EximMapperV1::permId, x -> x));

        // create assignments
        List<EximProblemDetailInvalidParamDTOV1> problems = new ArrayList<>();
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
    public RestResponse<EximProblemDetailResponseDTOV1> constraint(ConstraintViolationException ex) {
        return exceptionMapper.constraint(ex);
    }
}
