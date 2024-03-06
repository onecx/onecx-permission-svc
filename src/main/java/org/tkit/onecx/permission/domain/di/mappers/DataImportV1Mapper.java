package org.tkit.onecx.permission.domain.di.mappers;

import java.util.*;

import org.mapstruct.*;
import org.tkit.onecx.permission.domain.models.*;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportTenantRoleValueDTOV1;

@Mapper
public interface DataImportV1Mapper {

    default List<Assignment> createAssignments(Map<String, Set<String>> mapping, Map<String, Role> roles,
            Map<String, Permission> permissions) {
        if (permissions == null || roles == null || mapping == null) {
            return List.of();
        }
        List<Assignment> result = new ArrayList<>();
        mapping.forEach(
                (role, perms) -> perms.forEach(perm -> result.add(createAssignment(roles.get(role), permissions.get(perm)))));
        return result;
    }

    default Map<String, Set<String>> createMapping(Map<String, DataImportTenantRoleValueDTOV1> dtoRoles) {
        if (dtoRoles == null || dtoRoles.isEmpty()) {
            return Map.of();
        }
        Map<String, Set<String>> mapping = new HashMap<>();
        dtoRoles.forEach((role, item) -> {
            Set<String> perms = new HashSet<>();

            item.getAssignments()
                    .forEach((productName, apps) -> apps.forEach((appId, an) -> an.forEach((resource, actions) -> actions
                            .forEach(action -> perms.add(productName + appId + resource + action)))));

            mapping.put(role, perms);
        });
        return mapping;
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

    default List<Role> createRoles(Map<String, DataImportTenantRoleValueDTOV1> dto) {
        if (dto == null) {
            return List.of();
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "description", ignore = true)
    Application createApp(String appId, String name, String description, String productName);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "creationUser", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "modificationUser", ignore = true)
    @Mapping(target = "controlTraceabilityManual", ignore = true)
    @Mapping(target = "modificationCount", ignore = true)
    @Mapping(target = "persisted", ignore = true)
    @Mapping(target = "description", ignore = true)
    Permission createPermission(String appId, String resource, String action, String productName);
}
