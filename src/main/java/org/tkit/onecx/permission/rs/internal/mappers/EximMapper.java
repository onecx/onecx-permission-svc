package org.tkit.onecx.permission.rs.internal.mappers;

import java.time.OffsetDateTime;
import java.util.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tkit.onecx.permission.domain.models.Assignment;
import org.tkit.onecx.permission.domain.models.Permission;
import org.tkit.onecx.permission.domain.models.PermissionAction;
import org.tkit.onecx.permission.domain.models.Role;
import org.tkit.quarkus.rs.mappers.OffsetDateTimeMapper;

import gen.org.tkit.onecx.permission.rs.internal.model.AssignmentSnapshotDTO;
import gen.org.tkit.onecx.permission.rs.internal.model.ProblemDetailInvalidParamDTO;

@Mapper(uses = { OffsetDateTimeMapper.class })
public interface EximMapper {

    default AssignmentSnapshotDTO createSnapshot(List<PermissionAction> items) {
        AssignmentSnapshotDTO assignmentSnapshotDTO = new AssignmentSnapshotDTO();
        assignmentSnapshotDTO.setId(UUID.randomUUID().toString());
        assignmentSnapshotDTO.setCreated(OffsetDateTime.now());
        assignmentSnapshotDTO.setAssignments(createSnapshotAssignments(items));
        return assignmentSnapshotDTO;
    }

    default Map<String, Map<String, Map<String, Map<String, List<String>>>>> createSnapshotAssignments(
            List<PermissionAction> items) {

        if (items == null) {
            return Map.of();
        }
        Map<String, Map<String, Map<String, Map<String, List<String>>>>> result = new HashMap<>();

        for (PermissionAction permissionAction : items) {
            result
                    .computeIfAbsent(permissionAction.productName(), k -> new HashMap<>())
                    .computeIfAbsent(permissionAction.applicationId(), k -> new HashMap<>())
                    .computeIfAbsent(permissionAction.roleName(), k -> new HashMap<>())
                    .computeIfAbsent(permissionAction.resource(), k -> new ArrayList<>())
                    .add(permissionAction.action());
        }

        return result;
    }

    record RequestData(Map<String, List<String>> product, Set<String> roles) {
    }

    static String permId(Permission p) {
        return permId(p.getProductName(), p.getAppId(), p.getResource(), p.getAction());
    }

    static String permId(String productName, String appId, String resource, String action) {
        return productName + "#" + appId + "#" + resource + "#" + action;
    }

    default RequestData createRequestData(AssignmentSnapshotDTO dto) {
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

    default List<Assignment> createAssignments(List<ProblemDetailInvalidParamDTO> problems, AssignmentSnapshotDTO dto,
            Map<String, Role> roleMap, Map<String, Permission> permissionMap, List<Role> createRoles) {
        List<Assignment> assignments = new ArrayList<>();
        dto.getAssignments().forEach((productName, product) -> {
            if (product != null) {
                product.forEach((appId, app) -> {
                    if (app != null) {
                        assignments
                                .addAll(createProductAppAssignments(productName, appId, app, problems, roleMap, permissionMap,
                                        createRoles));
                    }
                });
            }
        });
        return assignments;
    }

    default List<Assignment> createProductAppAssignments(String productName, String appId,
            Map<String, Map<String, List<String>>> dto, List<ProblemDetailInvalidParamDTO> problems,
            Map<String, Role> roleMap, Map<String, Permission> permissionMap, List<Role> createRoles) {
        List<Assignment> assignments = new ArrayList<>();

        // application role - resource - actions
        for (var e : dto.entrySet()) {
            var roleName = e.getKey();

            var role = roleMap.get(roleName);
            if (role == null) {
                role = createRole(roleName);
                createRoles.add(role);
                roleMap.put(roleName, role);
            }

            for (Map.Entry<String, List<String>> me : e.getValue().entrySet()) {
                var resource = me.getKey();

                for (String action : me.getValue()) {
                    var permId = permId(productName, appId, resource, action);
                    var permission = permissionMap.get(permId);
                    if (permission == null) {
                        problems.add(createProblem("Permission not found", "Permission ID: " + permId));
                    } else {
                        var assignment = create(role, permission);
                        assignment.setOperator(true);
                        assignments.add(assignment);
                    }
                }
            }
        }

        return assignments;
    }

    @Mapping(target = "mandatory", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "operator", constant = "true")
    Role createRole(String name);

    ProblemDetailInvalidParamDTO createProblem(String name, String message);

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
}
