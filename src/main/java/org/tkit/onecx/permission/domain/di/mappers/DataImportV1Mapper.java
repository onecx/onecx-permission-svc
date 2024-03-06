package org.tkit.onecx.permission.domain.di.mappers;

import java.util.*;

import org.mapstruct.*;
import org.tkit.onecx.permission.domain.models.*;

import gen.org.tkit.onecx.permission.domain.di.v1.model.DataImportTenantRoleValueDTOV1;

@Mapper
public abstract class DataImportV1Mapper {

    public List<Assignment> createAssignments(Map<String, Set<String>> mapping, Map<String, Role> roles,
            Map<String, Permission> permissions) {
        if (permissions == null || roles == null || mapping == null) {
            return List.of();
        }
        List<Assignment> result = new ArrayList<>();
        mapping.forEach(
                (role, perms) -> perms.forEach(perm -> result.add(createAssignment(roles.get(role), permissions.get(perm)))));
        return result;
    }

    public Map<String, Set<String>> createMapping(Map<String, DataImportTenantRoleValueDTOV1> dtoRoles) {
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
    public abstract Assignment createAssignment(Role role, Permission permission);

    public List<Role> createRoles(Map<String, DataImportTenantRoleValueDTOV1> dto) {
        if (dto == null) {
            return List.of();
        }
        return dto.entrySet().stream().map(entry -> createRole(entry.getKey(), entry.getValue().getDescription())).toList();
    }

    public Role updateRole(String description, Role role) {
        if (description != null) {
            role.setDescription(description);
        }
        return role;
    }

    public Role createRole(String name, String description) {
        var r = createRole(name);
        r.setDescription(description);
        return r;
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
    protected abstract Role createRole(String name);

    public Permission updatePermission(String description, Permission permission) {
        if (description != null) {
            permission.setDescription(description);
        }
        return permission;
    }

    public Permission createPermission(String productName, String appId, String resource, String action, String description) {
        var perm = createPermission(productName, appId, resource, action);
        perm.setDescription(description);
        return perm;
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
    protected abstract Permission createPermission(String productName, String appId, String resource, String action);

    public Application createApp(String productName, String appId, String name, String description) {
        var app = createApp(appId, productName);
        app.setName(name);
        app.setDescription(description);
        return app;
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
    @Mapping(target = "name", ignore = true)
    protected abstract Application createApp(String appId, String productName);

    public Application updateApp(String name, String description, @MappingTarget Application application) {
        if (name != null) {
            application.setName(name);
        }
        if (description != null) {
            application.setDescription(description);
        }
        return application;
    }
}
