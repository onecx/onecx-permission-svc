package io.github.onecx.permission.domain.di.mappers;

import java.util.*;

import org.mapstruct.*;

import gen.io.github.onecx.permission.domain.di.v1.model.DataImportRoleDTOV1;
import io.github.onecx.permission.domain.models.*;

@Mapper
public interface DataImportMapperV1 {

    default List<Assignment> createAssignments(Map<String, Set<String>> mapping, Map<String, Role> roles,
            Map<String, Permission> permissions) {
        if (permissions == null || roles == null || mapping == null) {
            return null;
        }
        List<Assignment> result = new ArrayList<>();
        mapping.forEach(
                (role, perms) -> perms.forEach(perm -> result.add(createAssignment(roles.get(role), permissions.get(perm)))));
        return result;
    }

    default Map<String, Set<String>> createMapping(Map<String, DataImportRoleDTOV1> dtoRoles) {
        if (dtoRoles == null || dtoRoles.isEmpty()) {
            return null;
        }
        Map<String, Set<String>> mapping = new HashMap<>();
        dtoRoles.forEach((role, item) -> {
            Set<String> perms = new HashSet<>();

            item.getAssignments().forEach((appId, an) -> {
                an.forEach((resource, actions) -> {
                    actions.forEach(action -> perms.add(appId + resource + action));
                });
            });

            mapping.put(role, perms);
        });
        return mapping;
    }

    default Map<String, Set<String>> createWorkspaceMapping(Map<String, DataImportRoleDTOV1> dtoRoles) {
        if (dtoRoles == null || dtoRoles.isEmpty()) {
            return null;
        }
        Map<String, Set<String>> mapping = new HashMap<>();
        dtoRoles.forEach((role, item) -> {
            Set<String> perms = new HashSet<>();

            item.getWorkspacesAssignments().forEach((workspaceId, an) -> {
                an.forEach((resource, actions) -> {
                    actions.forEach(action -> perms.add(workspaceId + resource + action));
                });
            });

            mapping.put(role, perms);
        });
        return mapping;
    }

    default List<WorkspaceAssignment> createWorkspaceAssignments(Map<String, Set<String>> mapping, Map<String, Role> roles,
            Map<String, WorkspacePermission> permissions) {
        if (permissions == null || roles == null || mapping == null) {
            return null;
        }

        List<WorkspaceAssignment> result = new ArrayList<>();
        mapping.forEach((role, perms) -> perms.forEach(perm -> result.add(create(roles.get(role), permissions.get(perm)))));
        return result;
    }

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
    Assignment createAssignment(Role role, Permission permission);

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
    WorkspaceAssignment create(Role role, WorkspacePermission permission);

    default List<Role> createRoles(Map<String, DataImportRoleDTOV1> dto) {
        if (dto == null) {
            return null;
        }
        return dto.entrySet().stream().map(entry -> create(entry.getKey(), entry.getValue().getDescription())).toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    Role create(String name, String description);

    default List<WorkspacePermission> mapWorkspace(Map<String, Map<String, Map<String, String>>> permissions) {
        if (permissions == null) {
            return List.of();
        }
        List<WorkspacePermission> result = new ArrayList<>();
        permissions.forEach((workspaceId, perm) -> perm.forEach((resource, actions) -> actions
                .forEach((action, description) -> {
                    var tmp = mapWorkspace(workspaceId, resource, action);
                    if (tmp != null) {
                        tmp.setDescription(description);
                        result.add(tmp);
                    }
                })));
        return result;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "description", ignore = true)
    WorkspacePermission mapWorkspace(String workspaceId, String resource, String action);

    default List<Permission> map(Map<String, Map<String, Map<String, String>>> permissions) {
        if (permissions == null) {
            return List.of();
        }
        List<Permission> result = new ArrayList<>();
        permissions.forEach((appId, perm) -> perm.forEach((resource, actions) -> actions
                .forEach((action, description) -> {
                    var tmp = map(appId, resource, action);
                    if (tmp != null) {
                        tmp.setDescription(description);
                        result.add(tmp);
                    }
                })));
        return result;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "description", ignore = true)
    Permission map(String appId, String resource, String action);
}
