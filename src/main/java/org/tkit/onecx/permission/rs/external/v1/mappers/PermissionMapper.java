package org.tkit.onecx.permission.rs.external.v1.mappers;

import java.util.*;

import org.mapstruct.Mapper;
import org.tkit.onecx.permission.domain.models.Permission;

import gen.org.tkit.onecx.permission.rs.external.v1.model.ApplicationPermissionsDTOV1;
import gen.org.tkit.onecx.permission.rs.external.v1.model.ApplicationsPermissionsDTOV1;

@Mapper
public interface PermissionMapper {

    default ApplicationsPermissionsDTOV1 create(List<Permission> permissions) {
        return new ApplicationsPermissionsDTOV1().applications(createApps(permissions));
    }

    default List<ApplicationPermissionsDTOV1> createApps(List<Permission> permissions) {
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

}
