package io.github.onecx.permission.rs.external.v1.mappers;

import java.util.*;

import org.mapstruct.Mapper;

import gen.io.github.onecx.permission.rs.v1.model.ApplicationPermissionsDTOV1;
import io.github.onecx.permission.domain.models.Permission;

@Mapper
public interface PermissionMapper {

    default Map<String, Set<String>> permissions(List<Permission> permissions) {
        if (permissions == null) {
            return null;
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
