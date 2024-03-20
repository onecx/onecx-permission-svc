package org.tkit.onecx.permission.rs.external.v1.mappers;

import java.util.*;

import org.mapstruct.Mapper;
import org.tkit.onecx.permission.domain.models.Permission;

import gen.org.tkit.onecx.permission.rs.external.v1.model.ApplicationPermissionsDTOV1;

@Mapper
public interface PermissionMapper {

    default Map<String, Set<String>> permissions(List<Permission> permissions) {
        if (permissions == null) {
            return Map.of();
        }
        Map<String, Set<String>> result = new HashMap<>();
        permissions.forEach(permission -> result.computeIfAbsent(permission.getResource(), k -> new HashSet<>())
                .add(permission.getAction()));
        return result;
    }

    default ApplicationPermissionsDTOV1 create(String productName, String appId, Map<String, Set<String>> permissions) {
        return new ApplicationPermissionsDTOV1().productName(productName).appId(appId).permissions(permissions);
    }

    default ApplicationPermissionsDTOV1 create(String productName, String appId, List<Permission> permissions) {
        return create(productName, appId, permissions(permissions));
    }

}
