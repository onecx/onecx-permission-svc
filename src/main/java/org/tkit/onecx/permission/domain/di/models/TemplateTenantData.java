package org.tkit.onecx.permission.domain.di.models;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tkit.onecx.permission.domain.models.PermissionAction;
import org.tkit.onecx.permission.domain.models.Role;

public class TemplateTenantData {

    Map<String, Role> roles;
    Set<String> permissionActions;

    public TemplateTenantData(List<Role> roles, List<PermissionAction> permissionActions) {
        this.roles = roles.stream().collect(Collectors.toMap(Role::getName, x -> x));
        this.permissionActions = permissionActions.stream().map(TemplateTenantData::permissionId).collect(Collectors.toSet());
    }

    public Role getRole(String name) {
        return roles.get(name);
    }

    public boolean hasPermissionAction(String roleName, String productName, String applicationId, String resource,
            String action) {
        return permissionActions.contains(permissionId(roleName, productName, applicationId, resource, action));

    }

    private static String permissionId(String roleName, String productName, String applicationId, String resource,
            String action) {
        return roleName + "#" + productName + "#" + applicationId + "#" + resource + "#" + action;
    }

    private static String permissionId(PermissionAction pa) {
        return permissionId(pa.roleName(), pa.productName(), pa.applicationId(), pa.resource(), pa.action());
    }
}
