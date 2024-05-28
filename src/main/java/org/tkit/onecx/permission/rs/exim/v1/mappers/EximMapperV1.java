package org.tkit.onecx.permission.rs.exim.v1.mappers;

import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.Role;

import gen.org.tkit.onecx.permission.rs.exim.v1.model.AssignmentSnapshotDTOV1;
import gen.org.tkit.onecx.permission.rs.exim.v1.model.EximProblemDetailInvalidParamDTOV1;

@Mapper
public interface EximMapperV1 {

    @Mapping(target = "mandatory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "permissionId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", ignore = true)
    Assignment create(Role role, Permission permission);

    default List<Assignment> createAssignments(List<EximProblemDetailInvalidParamDTOV1> problems, AssignmentSnapshotDTOV1 dto,
            Map<String, Role> roleMap, Map<String, Permission> permissionMap) {
        List<Assignment> assignments = new ArrayList<>();
        dto.getAssignments().forEach((productName, product) -> {
            if (product != null) {
                product.forEach((appId, app) -> {
                    if (app != null) {
                        assignments
                                .addAll(createProductAppAssignments(productName, appId, app, problems, roleMap, permissionMap));
                    }
                });
            }
        });
        return assignments;
    }

    default List<Assignment> createProductAppAssignments(String productName, String appId,
            Map<String, Map<String, List<String>>> dto, List<EximProblemDetailInvalidParamDTOV1> problems,
            Map<String, Role> roleMap, Map<String, Permission> permissionMap) {
        List<Assignment> assignments = new ArrayList<>();

        // application role - resource - actions
        for (var e : dto.entrySet()) {
            var roleName = e.getKey();

            var role = roleMap.get(roleName);
            if (role == null) {
                problems.add(createProblem("Role not found", "Role name: " + roleName));
                continue;
            }

            e.getValue().forEach((resource, actions) -> actions.forEach(action -> {
                var permId = permId(productName, appId, resource, action);
                var permission = permissionMap.get(permId);
                if (permission == null) {
                    problems.add(createProblem("Permission not found", "Permission ID: " + permId));
                } else {
                    var assignment = create(role, permission);
                    assignment.setOperator(true);
                    assignments.add(assignment);
                }
            }));
        }

        return assignments;
    }

    EximProblemDetailInvalidParamDTOV1 createProblem(String name, String message);

    default RequestData createRequestData(AssignmentSnapshotDTOV1 dto) {
        Map<String, List<String>> result = new HashMap<>();
        Set<String> roles = new HashSet<>();
        dto.getAssignments().forEach((productName, product) -> {
            if (product != null && !product.keySet().isEmpty()) {
                result.computeIfAbsent(productName, k -> new ArrayList<>()).addAll(product.keySet());
                product.forEach((appId, app) -> {
                    if (app != null) {
                        roles.addAll(app.keySet());
                    }
                });
            }
        });

        return new RequestData(result, roles);
    }

    static String permId(Permission p) {
        return permId(p.getProductName(), p.getAppId(), p.getResource(), p.getAction());
    }

    static String permId(String productName, String appId, String resource, String action) {
        return productName + "#" + appId + "#" + resource + "#" + action;
    }

    record RequestData(Map<String, List<String>> product, Set<String> roles) {
    }
}
