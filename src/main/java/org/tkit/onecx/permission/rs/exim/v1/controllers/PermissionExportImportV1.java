package org.tkit.onecx.permission.rs.exim.v1.controllers;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.tkit.onecx.permission.domain.daos.PermissionDAO;
import org.tkit.onecx.permission.domain.daos.RoleDAO;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.onecx.permission.domain.services.AssignmentService;
import org.tkit.onecx.permission.rs.exim.v1.mappers.EximExceptionMapperV1;
import org.tkit.onecx.permission.rs.exim.v1.mappers.EximMapperV1;
import org.tkit.quarkus.log.cdi.LogService;

import gen.org.tkit.onecx.permission.rs.exim.v1.PermissionExportImportApi;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.AssignmentSnapshotDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.EximProblemDetailInvalidParamDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.EximProblemDetailResponseDTOV1;

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

    @Override
    public Response operatorImportAssignments(AssignmentSnapshotDTOV1 assignmentSnapshotDTO) {

        Map<String, List<String>> productNames = new HashMap<>();
        Set<String> roleNames = new HashSet<>();

        assignmentSnapshotDTO.getAssignments().forEach((productName, product) -> {
            if (product != null) {
                productNames.computeIfAbsent(productName, k -> new ArrayList<>()).addAll(product.keySet());
                product.forEach((appId, app) -> {
                    if (app != null) {
                        roleNames.addAll(app.keySet());
                    }
                });
            }
        });

        // map of roles for assignments
        var roles = roleDAO.findByNames(roleNames);
        var roleMap = roles.stream().collect(Collectors.toMap(Role::getName, x -> x));

        // map of permissions for products
        var permissions = permissionDAO.findByProductNames(productNames.keySet());
        var permissionMap = permissions.stream().collect(Collectors.toMap(EximMapperV1::permId, x -> x));

        List<EximProblemDetailInvalidParamDTOV1> problems = new ArrayList<>();

        // create assignments
        List<Assignment> assignments = new ArrayList<>();
        assignmentSnapshotDTO.getAssignments().forEach((productName, product) -> {
            if (product != null) {
                product.forEach((appId, app) -> {
                    if (app != null) {
                        for (var e : app.entrySet()) {
                            var roleName = e.getKey();

                            var role = roleMap.get(roleName);
                            if (role == null) {
                                problems.add(exceptionMapper.createProblem("Role not found", "Role name: " + roleName));
                                continue;
                            }

                            e.getValue().forEach((resource, actions) -> actions.forEach(action -> {
                                var permId = EximMapperV1.permId(productName, appId, resource, action);
                                var permission = permissionMap.get(permId);
                                if (permission == null) {
                                    problems.add(exceptionMapper
                                            .createProblem("Permission not found", "Permission ID: " + permId));
                                } else {
                                    var assignment = mapper.create(role, permission);
                                    assignments.add(assignment);
                                }
                            }));
                        }
                    }
                });
            }
        });

        // delete old and create new assignments
        service.importOperator(assignments, productNames);

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
