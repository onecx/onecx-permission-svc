package io.github.onecx.permission.domain.di;

import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.tkit.quarkus.context.ApplicationContext;
import org.tkit.quarkus.context.Context;

import gen.io.github.onecx.permission.domain.di.v1.model.DataImportTenantWrapperDTOV1;
import io.github.onecx.permission.domain.daos.*;
import io.github.onecx.permission.domain.di.mappers.DataImportMapperV1;
import io.github.onecx.permission.domain.models.Permission;
import io.github.onecx.permission.domain.models.Role;

@ApplicationScoped
public class PermissionImportService {

    @Inject
    PermissionDAO permissionDAO;

    @Inject
    AssignmentDAO assignmentDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    WorkspacePermissionDAO workspacePermissionDAO;

    @Inject
    WorkspaceAssignmentDAO workspaceAssignmentDAO;

    @Inject
    DataImportMapperV1 mapper;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteAllData(String tenantId) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            assignmentDAO.deleteQueryAll();
            workspaceAssignmentDAO.deleteQueryAll();
            workspacePermissionDAO.deleteQueryAll();
            roleDAO.deleteQueryAll();

        } finally {
            ApplicationContext.close();
        }
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void deleteAllPermissions() {
        permissionDAO.deleteQueryAll();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Map<String, Permission> createAllPermissions(Map<String, Map<String, Map<String, String>>> permissions) {
        var items = mapper.map(permissions);
        permissionDAO.create(items);
        return items.stream().collect(Collectors.toMap(r -> r.getAppId() + r.getResource() + r.getAction(), r -> r));
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTenantData(String tenantId, DataImportTenantWrapperDTOV1 dto, Map<String, Permission> permissionMap) {
        try {
            var ctx = Context.builder()
                    .principal("data-import")
                    .tenantId(tenantId)
                    .build();

            ApplicationContext.start(ctx);

            // create workspace permissions
            var workspacePermissions = mapper.mapWorkspace(dto.getWorkspacesPermissions());
            workspacePermissionDAO.create(workspacePermissions);
            var workspacePermissionsMap = workspacePermissions.stream()
                    .collect(Collectors.toMap(r -> r.getWorkspaceId() + r.getResource() + r.getAction(), r -> r));

            // create tenant roles
            var roles = mapper.createRoles(dto.getRoles());
            roleDAO.create(roles);
            var rolesMap = roles.stream().collect(Collectors.toMap(Role::getName, r -> r));

            // create tenant assignments
            var mapping = mapper.createMapping(dto.getRoles());
            var assignments = mapper.createAssignments(mapping, rolesMap, permissionMap);
            assignmentDAO.create(assignments);

            // create tenant workspace assignments
            var workspaceMapping = mapper.createWorkspaceMapping(dto.getRoles());
            var workspaceAssignments = mapper.createWorkspaceAssignments(workspaceMapping, rolesMap, workspacePermissionsMap);
            workspaceAssignmentDAO.create(workspaceAssignments);

        } finally {
            ApplicationContext.close();
        }
    }
}
