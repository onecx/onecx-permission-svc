package io.github.onecx.permission.rs.external.v1.mappers;

import java.util.*;

import org.mapstruct.Mapper;

import gen.io.github.onecx.permission.rs.external.v1.model.ApplicationPermissionsDTOV1;
import gen.io.github.onecx.permission.rs.external.v1.model.WorkspacePermissionApplicationsDTOV1;
import gen.io.github.onecx.permission.rs.external.v1.model.WorkspacePermissionsDTOV1;
import io.github.onecx.permission.domain.models.Permission;
import io.github.onecx.permission.domain.models.WorkspacePermission;

@Mapper
public interface PermissionMapper {

    default WorkspacePermissionApplicationsDTOV1 createWorkspaceApps(String workspaceId,
            List<WorkspacePermission> workspacePermissions, List<Permission> permissions) {
        return new WorkspacePermissionApplicationsDTOV1()
                .workspace(createWorkspace(workspaceId, workspacePermissions))
                .applications(splitToApps(permissions));
    }

    default List<ApplicationPermissionsDTOV1> splitToApps(List<Permission> permissions) {
        if (permissions == null) {
            return List.of();
        }
        Map<String, List<Permission>> items = new HashMap<>();
        permissions.forEach(permission -> items.computeIfAbsent(permission.getAppId(), k -> new ArrayList<>())
                .add(permission));
        return items.entrySet().stream().map(e -> create(e.getKey(), e.getValue())).toList();
    }

    default Map<String, Set<String>> permissions(List<Permission> permissions) {
        if (permissions == null) {
            return Map.of();
        }
        Map<String, Set<String>> result = new HashMap<>();
        permissions.forEach(permission -> result.computeIfAbsent(permission.getResource(), k -> new HashSet<>())
                .add(permission.getAction()));
        return result;
    }

    default ApplicationPermissionsDTOV1 create(String appId, Map<String, Set<String>> permissions) {
        return new ApplicationPermissionsDTOV1().appId(appId).permissions(permissions);
    }

    default ApplicationPermissionsDTOV1 create(String appId, List<Permission> permissions) {
        return create(appId, permissions(permissions));
    }

    default Map<String, Set<String>> permissionsWorkspace(List<WorkspacePermission> permissions) {
        if (permissions == null) {
            return Map.of();
        }
        Map<String, Set<String>> result = new HashMap<>();
        permissions.forEach(permission -> result.computeIfAbsent(permission.getResource(), k -> new HashSet<>())
                .add(permission.getAction()));
        return result;
    }

    default WorkspacePermissionsDTOV1 createWorkspace(String workspaceId, Map<String, Set<String>> permissions) {
        return new WorkspacePermissionsDTOV1().workspaceId(workspaceId).permissions(permissions);
    }

    default WorkspacePermissionsDTOV1 createWorkspace(String workspaceId, List<WorkspacePermission> permissions) {
        return createWorkspace(workspaceId, permissionsWorkspace(permissions));
    }
}
